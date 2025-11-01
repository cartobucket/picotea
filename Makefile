.PHONY: help build run testProgressBars

help:
	@echo "Available targets:"
	@echo "  build             - Build the project"
	@echo "  run               - Run the main application"
	@echo "  testProgressBars  - Run various progress bar examples"
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
