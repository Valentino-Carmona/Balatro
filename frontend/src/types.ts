export interface CardDTO {
  suit: string;
  rank: string;
  points: number;
  mult: number;
  addMult: number;
}

export interface PlayerDTO {
  name: string;
  money: number;
  handCards: CardDTO[];
  jokers?: JokerDTO[];
  tarots?: TarotDTO[];
}

export interface RoundDTO {
  handsLeft: number;
  discardsLeft: number;
  playerScore: number;
  targetScore: number;
}

export interface JokerDTO {
  name: string;
  description: string;
  cost: number;
}

export interface TarotDTO {
  name: string;
  description: string;
  cost: number;
}

export interface StoreDTO {
  jokers: JokerDTO[];
  tarots: TarotDTO[];
  cards: CardDTO[];
}

export interface GameSessionDTO {
  sessionId: string;
  player: PlayerDTO;
  round: RoundDTO;
  currentAnte: number;
  currentBlind: number;
  gameOver: boolean;
  store: StoreDTO;
}

export interface ScoreEventDTO {
  type: string;
  name: string;
  points: number;
  multiplier: number;
  currentTotalPoints: number;
  currentTotalMult: number;
}

export interface ScoreResponseDTO {
  points: number;
  multiplier: number;
  totalScore: number;
  handName: string;
  events?: ScoreEventDTO[];
}

export interface PlayResponseDTO {
  gameState: GameSessionDTO;
  handScore: ScoreResponseDTO;
  roundWon: boolean;
}
