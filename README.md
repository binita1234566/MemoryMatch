# Memory Match

An Android memory game built with Kotlin and XML layouts.

## How to play

Enter your name and optionally take a photo for your avatar.
Find all six matching fruit pairs before your 20 attempts run out.

Each pair of selected cards uses one attempt. Matching cards stay
visible. For a mismatch, review the cards and press Hide Cards
before continuing.

Finding all six pairs wins the game. Running out of attempts
without finding all pairs loses the game.

## Running the project

Open the project in Android Studio, let Gradle sync, and run the
app on an Android emulator or a compatible Android device.

## Challenge features

| # | Challenge | Implementation |
|---|-----------|----------------|
| 1 | Restart | Restart Game and Play Again reset the board. |
| 2 | Rotation | Saved instance state preserves the board, selections, matches, and attempts. |
| 3 | Second screen | MainActivity passes the player's name to GameActivity. |
| 4 | Array display | A shuffled array supplies the interactive fruit cards. |
| 5 | Text entry | The entered name appears in the game and shared results. |
| 6 | Easter egg | Long-press the player's heading during an active game to gain five attempts. |
| 7 | App bar | The toolbar includes a Help action. |
| 8 | Implicit intent | Share Score opens Android's sharing chooser. |
| 9 | Camera intent | The camera captures an avatar displayed on both screens. |
| 10 | Localization | English and Spanish string resources cover the interface and game messages. |
| 11 | TalkBack | Cards have spoken positions and states. Mismatches remain visible until Hide Cards is activated. |
| 12 | Custom View | FruitBasketView overrides onDraw to draw the basket and collected fruit. |
| 13 | Picture using shapes | Canvas shapes and colors form a fruit basket that fills as pairs are matched. |
| 14 | Animation | Selected cards animate their scale. |
| 15 | AnimatorSet | A basket bounce is followed by a wiggle using playSequentially. |
| 16 | Touch interaction | Tapping the moving basket stops its animation. |
| 17 | App icon | A custom purple MM launcher icon identifies the app. |
| 18 | Dynamic text | Matches, attempts, status, and the restart button update during play. |

## Testing the features

- Rotate the device during a turn and check that progress remains.
- Test both winning and losing, then restart.
- Share a result and compare it with the displayed score.
- Take an avatar photo and check both screens.
- Enable TalkBack and play using the spoken card descriptions.
- Set the device's primary language to Spanish to test translations.
- Find a match to animate the basket, then tap it while it moves.
- Long-press the player heading to test the hidden bonus.
