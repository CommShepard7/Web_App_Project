package game.gameinterfaces;

public enum GameAction {
	/*
	 * QUASAR ACTIONS
	 * Choose a random value in [1,8] or [4,7]
	 */
	
	ONEToEIGHT,FOURToSEVEN,
	
	/*
	 * PAZAAK ACTIONS
	 */
	
	/* Special cards */

	minusOne,minusTwo,minusThree,
	minusFour,minusFive,minusSix,
	plusOne,plusTwo,plusThree,
	plusFour,plusFive,plusSix,
	
	/* Actions */
	MINUS,PLUS,ENDTURN
}
