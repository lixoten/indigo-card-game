// C:\Users\lixot\IdeaProjects\Indigo Card Game\Indigo Card Game\task\src\indigo
// Stage 4/5: The game logic Stage 4/5: The game logic Stage 4/5: The game logic
package indigo

import kotlin.system.exitProcess

enum class RANKS(val rank: String) {
    ACE("A"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    JACK("J"),
    QUEEN("Q"),
    KING("K");
}

enum class SUITS(val suit: String) {
    HEARTS("♥"),
    CLUBS("♣"),
    DIAMONDS("♦"),
    SPADES("♠");
}

enum class MENU(val action: String) {
    SHOW("show"),
    RESET("get"),
    SHUFFLE("shuffle"),
    GET("get"),
    EXIT("exit"),
}
const val CARD_PER_DECK = 52

val myRegex2 = Regex("[1-9]")


class IndigoGame{
    val newDeck = mutableListOf<String>()
    var workingDeck = mutableListOf<String>()

    var totalCardsLeftCnt = CARD_PER_DECK
    var totalCardsTakenCnt = 0

    var cardsOnTable = mutableListOf<String>()
    var playerOneHand = mutableListOf<String>()
    var playerTwoHand = mutableListOf<String>()
    var playerOneWonCards = mutableListOf<String>() // tracking cards won
    var playerTwoWonCards = mutableListOf<String>() // tracking cards won

    var lastHandWinner = 9

    // tally
    var playerOneWins = 0
    var playerTwoWins = 0

    init {
    	initializeGame()

        println("Indigo Card Game")
    }

    fun setFirstPlayer(): Boolean {
        val validResponses = listOf("yes", "y", "no", "n", "exit", "e")
        var response: String
        do{
            println("Play first?")
            response = readln().lowercase()
        } while (response !in validResponses)

        return when (response) {
            "y","yes" -> true
            "n","no" -> false
            else -> {
                endGame()
                false
            }
        }
    }

    fun initialDeal(){
        dealCards()
        shuffleCards()
        print("Initial cards on the table: ")
        //val deal = getCards(4) // 4 cards random
        placeCardOnTable(getCards(4))
        for (i in cardsOnTable.indices){
            print("${cardsOnTable[i]} ")
        }
        println()
    }

    fun initialGameVariable(player: Boolean) {
        // Initialize this for player who played first card. RARE,in case no player wins a card.
        lastHandWinner = if (player) 1 else 2
    }

    fun chooseAction() {
        do {
            playGame()
            //var cardsOnHandCnt = 0
            val cardsOnDeckCnt = 0 // Problem, no idea why. not used but if i remove i get a error on test cases

            println("Choose an action (reset, shuffle, get, exit):")
            val action = readln()
            when (action) {
                MENU.SHOW.action -> showDeck()
                "play" -> playGame()
                "reset" -> dealCards()
                "shuffle" -> shuffleCards()
                "get" -> getTopCard()
                "exit" -> endGame()
                else -> println("Wrong action.")
            }
        } while (action != "exit")
    }


    fun playGame() {
        var firstPlayer = setFirstPlayer()

        initialGameVariable(firstPlayer)
        initialDeal()

        //var cardPlayed = 0
        //val myRegex2 = Regex("[1-9]")

        do {
            if (cardsOnTable.isEmpty()) {
                println()//111
                println("No cards on the table")
            } else {
                println()//111
                println("${cardsOnTable.size} cards on the table, and the top card is ${cardsOnTable[cardsOnTable.lastIndex]}")
            }

            if (cardsOnTable.size == 52) {
                endGame()
            }

            val cardFaceValue = selectCard(firstPlayer)

            //var handWinner: Boolean

            val handWinner = analyzeHandForWin(cardFaceValue)

            placeCardOnTableV2(cardFaceValue)
            removeHandCard(firstPlayer, cardFaceValue)

            // A bit of clean up on tallies
            if (handWinner) {
                lastHandWinner = if (firstPlayer) 1 else 2
            }

            if (handWinner){
                updateTally(firstPlayer)
                displayTallyBoardV2(firstPlayer)
            }

            firstPlayer = !firstPlayer
        } while (true)
    }

    fun selectCard(player: Boolean): String {
        var playerHand: MutableList<String>
        //var playerWonCards: MutableList<String>
        //var playerWins: Int

        var cardPlayed = 0

        playerHand = if (player) playerOneHand else playerTwoHand

        if (playerHand.isEmpty()) {
            playerHand = getCards(6)
            if (playerHand.isEmpty()) {
                awardRemainingCards()
                //updateTally(player)
                displayTallyBoardV2(player, true)
                endGame()
                endGame("""
                    empty because no more cards in working deck
                    -- these cards are Left on table
                    -- \n${cardsOnTable}
                    """)
            }
        }

        if (player) {
            var tmp = ""
            print("Cards in hand: ")
            for (i in playerHand.indices){
                tmp += "${i + 1})${playerHand[i]} "
            }
            print(tmp.trim())
            println()

            do{
                println("Choose a card to play (1-${playerHand.size}):")
                val tmp = readln()

                if (tmp == "exit") endGame()

                if (myRegex2.matches(tmp)) {
                    cardPlayed = tmp.toInt()
                }
            } while (cardPlayed !in 1..playerHand.size)
        } else {
            cardPlayed = (1..playerHand.size).random()
            println("Computer plays ${playerHand[cardPlayed - 1]}")
        }

        val cardFaceValue = playerHand[cardPlayed - 1]

        // Update Properties
        if (player) {
            playerOneHand = playerHand
        } else {
            playerTwoHand = playerHand
        }

        return cardFaceValue //cardPlayed
    }


    fun initializeGame() {
        for (s in SUITS.values()) {
            for (r in RANKS.values()) {
                newDeck.add(r.rank + s.suit)
            }
            newDeck.toList()
        }
    }

    fun getCards(num:Int): MutableList<String> {
        if (workingDeck.isEmpty()) {
            return mutableListOf()
        }

        val x = mutableListOf<String>()
        for (i in 0 until num) {
            x.add(workingDeck.get(workingDeck.lastIndex))
            workingDeck.removeAt(workingDeck.lastIndex)
        }
        return x
    }

//    fun getCardsV2(num:Int): MutableList<String> {
//        if (workingDeck.isEmpty()){
//            return mutableListOf<String>()
//        } else {
//            val x = mutableListOf<String>()
//            for (i in 0 until num) {
//                x.add(workingDeck.get(workingDeck.lastIndex))
//                workingDeck.removeAt(workingDeck.lastIndex)
//            }
//            return x
//        }
//    }

    fun placeCardOnTable(deal: MutableList<String>) {
        for (n in deal){
            cardsOnTable.add(n)
        }
    }
    fun placeCardOnTableV2(card: String) {//deal: MutableList<String>
        cardsOnTable.add(card)
    }

    fun removeHandCard(player: Boolean, card: String) {//deal: MutableList<String>
        if (player) playerOneHand.remove(card) else playerTwoHand.remove(card)
    }

    fun tallyRoundScore() :Int {
        val tempArr1 = listOf("A♥", "10♥", "J♥", "Q♥", "K♥") //A, 10, J, Q, K get 1 p
        val tempArr2 = listOf("A♣", "10♣", "J♣", "Q♣", "K♣") //A, 10, J, Q, K get 1 p
        val tempArr3 = listOf("A♦", "10♦", "J♦", "Q♦", "K♦") //A, 10, J, Q, K get 1 p
        val tempArr4 = listOf("A♠", "10♠", "J♠", "Q♠", "K♠") //A, 10, J, Q, K get 1 p
        val tempArr = tempArr1 + tempArr2 + tempArr3 + tempArr4

        var cnt = 0
        for (n in cardsOnTable){
            if (tempArr.contains(n))
                cnt++
        }

        return cnt
    }


    fun displayTallyBoardV2(firstPlayer: Boolean, end: Boolean = false) {
        if (!end) {
            if (firstPlayer){
                println("Player wins cards")
            } else {
                println("Computer wins cards")
            }
        }
        println("Score: Player $playerOneWins - Computer $playerTwoWins")
        println("Cards: Player ${playerOneWonCards.size} - Computer ${playerTwoWonCards.size}")
    }

    fun updateTally(player: Boolean) {
        if (player){
            playerOneWonCards += cardsOnTable
            playerOneWins += tallyRoundScore()
        } else {
            playerTwoWonCards += cardsOnTable
            playerTwoWins += tallyRoundScore()
        }
        cardsOnTable.clear()
    }

    fun awardRemainingCards() {
        if (lastHandWinner == 1){
            playerOneWonCards += cardsOnTable
            playerOneWins += tallyRoundScore()
        } else {
            playerTwoWonCards += cardsOnTable
            playerTwoWins += tallyRoundScore()
        }

        if (playerOneWonCards.size > playerTwoWonCards.size) {
            playerOneWins += 3
        } else {
            playerTwoWins += 3
        }

        cardsOnTable.clear()
    }


    fun analyzeHandForWin(playedCard: String): Boolean { //topCard: String,
        if (cardsOnTable.isEmpty()) return false

        val topCard = cardsOnTable[cardsOnTable.lastIndex]
        val suitTopCard = topCard[topCard.lastIndex]
        val (rankTopCard, b) = topCard.split(suitTopCard)

        val suitPlayedCard = playedCard[playedCard.lastIndex]
        val (rankPlayedCard, c) = playedCard.split(suitPlayedCard)

        if (rankTopCard == rankPlayedCard || suitTopCard == suitPlayedCard) {
            return true
        }
        return false
    }


    fun showDeck() {
        var cnt = 0
        for (idx in 0 until newDeck.size) {
            cnt++
            if (cnt == 14 ) {
                print("\n" +newDeck[idx]+"  ")
                cnt = 1
            } else {
                print(newDeck[idx]+"  ")
            }
        }
        println("\n=================================================================================================")
        cnt = 0
        for (idx in 0 until workingDeck.size) {
            cnt++
            if (cnt == 14 ) {
                print("\n" +workingDeck[idx]+"  ")
                cnt = 1
            } else {
                print(workingDeck[idx]+"  ")
            }
        }
        println("\n=================================================================================================")
    }

    fun dealCards() {
        workingDeck = newDeck.toMutableList()
        totalCardsLeftCnt = CARD_PER_DECK
        totalCardsTakenCnt = 0
        //println("Card deck is reset.")
    }

    fun shuffleCards() {
        workingDeck.shuffle()
        //println("Card deck is shuffled.")
    }

    fun getTopCard() {
        var num = 0

        val myRegex = Regex(
            "[1-9]|" +       // 1.  1 to 9 --> [0-9]
                    "[12][0-9]|" +  // 2. 10 to 29 --> [12][0-9]
                    "3[0-9]|" +     // 3. 30 to 39 --> [3][0-9]
                    "4[0-9]|" +     // 4. 40 to 49 --> [4][0-9]
                    "5[0-2]")       // 5. 50 to 52 --> 5[02]
        do {
            println("Number of cards:")
            val tmp = readln()
            if (myRegex.matches(tmp)) {
                num = tmp.toInt()
                if (num < 0 || num > 53) {
                    println("Invalid number of cards.")
                }
                break
            } else {
                println("Invalid number of cards.")
            }
        } while (false)

        if (totalCardsTakenCnt + num > totalCardsLeftCnt) {
            println("The remaining cards are insufficient to meet the request.")
        } else {

            var cnt = totalCardsLeftCnt
            for (n in 0 until num) {
                cnt--
                print("${workingDeck[cnt]} ")
                workingDeck.removeAt(cnt)
            }
            totalCardsTakenCnt += num
            totalCardsLeftCnt -= num

            println()
        }

        chooseAction()
        return
    }

    fun endGame(msg: String = "") {
        if (msg != "") {
            println(msg)
        }
        println("Game Over")
        //println("Bye")
        exitProcess(0)
    }
}


fun main() {
    val gameObj = IndigoGame()
    //gameObj.setupDeck()
    gameObj.chooseAction()
}
// 453 482 468 351 281