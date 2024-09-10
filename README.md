# RollHelper

RollHelper is an Android application designed to help tabletop gamers with their dice rolls. Built with Jetpack Compose and Material Design 3, the app provides an intuitive, visually appealing interface for selecting dice types, rolling them, and viewing the results. It's a handy tool for Dungeons & Dragons (D&D) players and other tabletop RPG enthusiasts who want to simplify dice rolling and keep track of their roll history.

## Features

- **Select Dice Type**: Choose from a range of dice types (D4, D6, D8, D10, D12, D20).
- **Adjust Dice Count**: Use a slider to select the number of dice to roll.
- **Animated Dice Rolls**: Visualize the rolling process with smooth animations.
- **Roll History**: Keep a record of your recent rolls, including the dice type, count, and total.
- **User-Friendly Interface**: A clean and simple design built with Jetpack Compose and Material Design 3.
- **Customizable Theme**: Dynamic colors and custom fonts to match the theme of your adventure.

## Screenshots

<img src="https://github.com/user-attachments/assets/033f9853-2838-4024-8c3e-998b69d6b21a" alt="App Screenshot" width="50%">


## Installation

To run the RollHelper app locally, follow these steps:

1. **Clone the repository**:
    ```bash
    git clone https://github.com/yourusername/RollHelper.git
    ```

2. **Open the project in Android Studio**:
    - Ensure you have the latest version of [Android Studio](https://developer.android.com/studio) installed.

3. **Build the project**:
    - Sync the Gradle files and build the project.

4. **Run the app**:
    - Connect an Android device or use an emulator, then click the 'Run' button in Android Studio.

## Usage

- **Select Dice Type**: Click on any dice type chip (D4, D6, D8, D10, D12, D20) to select the type of dice you want to roll.
- **Set Dice Count**: Use the slider to adjust the number of dice to roll.
- **Roll the Dice**: Press the "Roll" button to see the animated result of your roll.
- **View Roll History**: Scroll down to see a list of your most recent rolls and their results.

## Technologies Used

- **Kotlin**: The primary language used for development.
- **Jetpack Compose**: Android’s modern toolkit for building native UI.
- **Material Design 3**: For consistent UI components and theming.
- **Coroutines**: For managing asynchronous operations and concurrency.
- **Snackbar**: To display brief messages at the bottom of the screen.

## File Structure

- **`MainActivity.kt`**: The entry point of the app.
- **`DiceRollScreen.kt`**: Contains the main composable screen with the UI logic.
- **`DiceTypeChips.kt`**: Composable to select the dice type.
- **`DiceCountSlider.kt`**: Composable to adjust the number of dice.
- **`RollButton.kt`**: Composable button for rolling the dice.
- **`DiceResultDisplay.kt`**: Composable to display dice results.
- **`RollHistorySection.kt`**: Composable to display roll history.
- **`DiceFace.kt`**: Handles the animation and display of dice faces.

## Future Enhancements

- **More Dice Types**: Add more specialized dice types for various tabletop games.
- **Custom Dice Sets**: Allow users to create and save custom sets of dice.
- **Sound Effects**: Enhance user experience with rolling sound effects.
- **Dark Mode**: Improve visual accessibility with a dark mode option.
- **Multiplayer Mode**: Share and sync rolls with other players in real time.
- **Accessibility Improvements**: Support for screen readers and improved color contrast.


## Acknowledgments

- Thanks to the open-source community for the libraries and tools that made this project possible.
- Inspiration from the Dungeons & Dragons and tabletop gaming community.

