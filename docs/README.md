# pico-tea Documentation

Welcome to the pico-tea documentation! This library provides reusable components for picocli command-line applications.

## Documentation Index

### Getting Started
- [Getting Started Guide](getting-started.md) - Start here if you're new to pico-tea

### Components
- [ProgressBar](components/ProgressBar.md) - Customizable progress bars with multiple styles

### Guides
- [Project Overview](../README.md) - Main project README
- [Building and Running](getting-started.md#installation) - Setup instructions

## Quick Links

### Examples
All examples can be found in the `src/main/kotlin/com/cartobucket/examples/` directory.

Run examples:
```bash
./gradlew run
```

### API Documentation
Component APIs are documented in their respective component pages:
- [ProgressBar API](components/ProgressBar.md#api-reference)

### Project Structure
```
pico-tea/
├── components/          # Reusable component library
├── src/                # Examples and demos
├── docs/               # This documentation
└── Makefile           # Build shortcuts
```

## Contributing to Documentation

When adding new components:
1. Create a new markdown file in `docs/components/`
2. Include: Quick Start, API Reference, Examples
3. Update this README with a link
4. Update the main README.md

## Documentation Style Guide

- Use code blocks for examples
- Include both simple and advanced usage
- Document all public API methods
- Provide working code examples
- Link between related pages
