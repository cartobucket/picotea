.PHONY: help build run testProgressBars testSpinners

help:
	@echo "Available targets:"
	@echo "  build             - Build the project"
	@echo "  run               - Run the main application"
	@echo "  testProgressBars  - Run various progress bar examples"
	@echo "  testSpinners      - Run various spinner examples"
	@echo "  clean             - Clean build artifacts"

build:
	./gradlew build

run:
	./gradlew run

clean:
	./gradlew clean

testProgressBars:
	@echo "======================================"
	@echo "Progress Bar Examples"
	@echo "======================================"
	@echo ""
	@echo "1. Basic progress bar (default settings)"
	@echo "--------------------------------------"
	./gradlew run --args="progressBar --end 50" --console=plain --quiet
	@echo ""
	@sleep 1
	@echo "2. With rate and ETA display"
	@echo "--------------------------------------"
	./gradlew run --args="progressBar --end 100 --step-size 5 --show-rate --show-eta" --console=plain --quiet
	@echo ""
	@sleep 1
	@echo "3. ASCII style"
	@echo "--------------------------------------"
	./gradlew run --args="progressBar --style ASCII --end 60 --delay 30" --console=plain --quiet
	@echo ""
	@sleep 1
	@echo "4. Minimal style with custom label"
	@echo "--------------------------------------"
	./gradlew run --args="progressBar --style MINIMAL --label 'Loading data' --end 40 --delay 40" --console=plain --quiet
	@echo ""
	@sleep 1
	@echo "5. DOTS style"
	@echo "--------------------------------------"
	./gradlew run --args="progressBar --style DOTS --end 50 --delay 35" --console=plain --quiet
	@echo ""
	@sleep 1
	@echo "6. With status messages"
	@echo "--------------------------------------"
	./gradlew run --args="progressBar --label 'Downloading' --end 60 --with-messages --show-rate --unit files" --console=plain --quiet
	@echo ""
	@sleep 1
	@echo "7. Fast progress with large steps"
	@echo "--------------------------------------"
	./gradlew run --args="progressBar --end 1000 --step-size 50 --delay 20 --show-rate" --console=plain --quiet
	@echo ""
	@sleep 1
	@echo "8. Indeterminate mode (spinner)"
	@echo "--------------------------------------"
	./gradlew run --args="progressBar --indeterminate --end 50 --delay 50 --label 'Processing'" --console=plain --quiet
	@echo ""
	@echo "======================================"
	@echo "All examples completed!"
	@echo "======================================"

testSpinners:
	@echo "======================================"
	@echo "Spinner Examples"
	@echo "======================================"
	@echo ""
	@echo "1. BRAILLE spinner (default)"
	@echo "--------------------------------------"
	./gradlew run --args="spinner --duration 3 --label 'Processing'" --console=plain --quiet
	@echo ""
	@sleep 1
	@echo "2. DOTS spinner"
	@echo "--------------------------------------"
	./gradlew run --args="spinner --style DOTS --duration 3 --label 'Loading'" --console=plain --quiet
	@echo ""
	@sleep 1
	@echo "3. LINE spinner (ASCII compatible)"
	@echo "--------------------------------------"
	./gradlew run --args="spinner --style LINE --duration 3 --label 'Working'" --console=plain --quiet
	@echo ""
	@sleep 1
	@echo "4. ARC spinner"
	@echo "--------------------------------------"
	./gradlew run --args="spinner --style ARC --duration 3 --label 'Calculating'" --console=plain --quiet
	@echo ""
	@sleep 1
	@echo "5. ARROW spinner"
	@echo "--------------------------------------"
	./gradlew run --args="spinner --style ARROW --duration 3 --label 'Syncing'" --console=plain --quiet
	@echo ""
	@sleep 1
	@echo "6. CIRCLE spinner"
	@echo "--------------------------------------"
	./gradlew run --args="spinner --style CIRCLE --duration 3 --label 'Downloading'" --console=plain --quiet
	@echo ""
	@sleep 1
	@echo "7. GROWING_DOTS spinner"
	@echo "--------------------------------------"
	./gradlew run --args="spinner --style GROWING_DOTS --duration 3 --label 'Building'" --console=plain --quiet
	@echo ""
	@sleep 1
	@echo "8. ELLIPSIS spinner"
	@echo "--------------------------------------"
	./gradlew run --args="spinner --style ELLIPSIS --duration 3 --label 'Waiting'" --console=plain --quiet
	@echo ""
	@sleep 1
	@echo "9. BOX spinner"
	@echo "--------------------------------------"
	./gradlew run --args="spinner --style BOX --duration 3 --label 'Compiling'" --console=plain --quiet
	@echo ""
	@sleep 1
	@echo "10. With status messages"
	@echo "--------------------------------------"
	./gradlew run --args="spinner --style BRAILLE --duration 5 --label 'Deploying' --with-messages" --console=plain --quiet
	@echo ""
	@sleep 1
	@echo "11. BOUNCE spinner"
	@echo "--------------------------------------"
	./gradlew run --args="spinner --style BOUNCE --duration 3 --label 'Testing'" --console=plain --quiet
	@echo ""
	@sleep 1
	@echo "12. BAR spinner"
	@echo "--------------------------------------"
	./gradlew run --args="spinner --style BAR --duration 3 --label 'Installing'" --console=plain --quiet
	@echo ""
	@echo "======================================"
	@echo "All spinner examples completed!"
	@echo "======================================"
