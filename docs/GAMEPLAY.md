# Balatro: Gameplay Mechanics and Rules

This document outlines the core gameplay mechanics, rules, and technical interactions of the Balatro clone project, derived from the original monolithic Java implementation.

## 1. Game Objective

Balatro is a deck-building roguelike inspired by poker. The main objective is to overcome successive rounds (blinds) by achieving a target score (`targetScore`). This score is calculated by playing standard poker hands combined with passive modifiers and multipliers.

The success of a run depends not only on the drawn cards but on the strategic combination of active and passive enhancements (Jokers, Tarot cards) purchased during the shop phase.

## 2. Core Mechanics

### Poker Hands
The player submits up to 5 cards per play. The system evaluates the hand to determine the best possible poker combination (Pair, Two Pair, Straight, Flush, Full House, etc.). 
Each valid poker hand provides:
* **Base Chips**: A fixed amount of chips depending on the hand type.
* **Base Multiplier**: A fixed multiplier applied to the base chips.

### Jokers (Passive Modifiers)
Jokers are permanent passive items that alter the scoring logic or game rules. They can provide:
* Exponential multipliers (e.g., "+4 Mult" or "x2 Mult").
* Extra chips under specific conditions (e.g., "+30 Chips if hand contains a Pair").
* Utility advantages (e.g., granting extra discards or money).

The evaluation of these modifiers relies heavily on the `JokerStrategy` hierarchy, processing each Joker sequentially during the scoring phase.

### Resource Management
During a round, the player is constrained by two primary resources:
* **Play Limit**: Maximum number of hands that can be played.
* **Discard Limit**: Maximum number of times a subset of cards can be discarded and replaced from the deck.

Beating a round within the constraints rewards the player with money, which is used in the Shop phase to purchase Jokers or consumable cards.

## 3. Game Loop and Interaction

The client-server architecture enforces an authoritative server model. The client acts as a visual interface, while all validation occurs on the backend.

1. **Round Start**: The server initializes the deck, draws the initial hand, and sets the target score.
2. **Player Action (Play)**: The player selects up to 5 cards and submits them via the `/api/v1/game/play` endpoint.
   * The server validates the hand.
   * Calculates base scores.
   * Sequentially applies all active Joker strategies.
   * Updates the accumulated score and deducts one play limit.
3. **Player Action (Discard)**: The player selects up to 5 cards to discard. The server removes them, draws replacements, and deducts one discard limit.
4. **Round End**: If the target score is reached, the round ends in victory, and the player proceeds to the shop. If the play limit reaches zero before the target score is met, the game ends in defeat.
5. **Shop Phase**: The player can spend accumulated money to buy Jokers, increasing their scoring potential for subsequent, harder rounds.

## 4. UI and Visual Feedback

While the backend processes the raw mechanics, the web client provides visual cues to reflect the game state:
* **Card Fan Layout**: Hands are dynamically rendered in a radial layout.
* **Synchronous Statistics**: The current score, required score, plays left, and discards left are updated immediately upon receiving the server's response.
* **Animations**: Visual feedback such as hover states and item selection highlight the interactive elements for clarity during decision-making.
