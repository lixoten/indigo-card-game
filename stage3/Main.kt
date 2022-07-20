// C:\Users\lixot\IdeaProjects\Indigo Card Game\Indigo Card Game\task\src\indigo
// Stage 3/5: The game is started v2
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

class IndigoGame{
    val newDeck = mutableListOf<String>()
    var workingDeck = mutableListOf<String>()

    var totalCardsLeftCnt = CARD_PER_DECK
    var totalCardsTakenCnt = 0

    var cardsOnTable = mutableListOf<String>()
    var playerOneHand = mutableListOf<String>()
    var playerTwoHand = mutableListOf<String>()

    // tally
    var playerOneWins = 0
    var playerTwoWins = 0

    init {
    	initializeGame()

        println("Indigo Card Game")
    }

    fun playFirstQuestion(): Boolean{
        var response = ""
        do{
            println("Play first?")
            response = readln().lowercase()
        } while (response != "yes" && response != "y" && response != "no")

        return response == "yes" || response == "y"
    }


    fun chooseAction() {
        do {
            dealCards()
            shuffleCards()

            val response = playFirstQuestion()

            var player = 0
            if (response == true) {
                player = 0 // you
            } else {
                player = 1 // computer
            }

            var deal = mutableListOf<String>()


            print("Initial cards on the table: ")
            deal = getCards(4) // 4 cards random
            //deal.shuffle() //cardPlayed = (0..playerTwoHand.lastIndex).random()
            placeCardOnTable(deal)
            for (i in cardsOnTable.indices){
                print("${cardsOnTable[i]} ")
            }
            println()


            var cardPlayed = 0
            val myRegex2 = Regex("[1-9]")

            do {
                if (cardsOnTable.isEmpty()) {
                    println()
                    println("No cards on the table")
                } else {
                    println()
                    println("${cardsOnTable.size} cards on the table, and the top card is ${cardsOnTable[cardsOnTable.lastIndex]}")
                }

                if (cardsOnTable.size == 52) {
                    endGame()
                }

                if (player == 0) { // 0 is you
                    if (playerOneHand.isEmpty()) {
                        playerOneHand = getCards(6)
                    }
                    print("Cards in hand: ")
                    for (i in playerOneHand.indices){
                        print("${i + 1})${playerOneHand[i]} ")
                    }
                    println()

                    do{
                        println("Choose a card to play (1-${playerOneHand.size}):")
                        //cardPlayed = readln().toInt()
                        val tmp = readln()

                        if (tmp == "exit") endGame()

                        if (myRegex2.matches(tmp)) {
                            cardPlayed = tmp.toInt()
                        }
                    } while (cardPlayed !in 1..playerOneHand.size)


                    //-------------------------
//                    val handWinner = tallyUpScore(player, cardsOnTable[cardsOnTable.lastIndex], playerOneHand[cardPlayed - 1])
//                    if (handWinner){
//                        displayTallyBoard(player)
//                    } else {
                        var tmp = mutableListOf<String>(playerOneHand[cardPlayed - 1])
                        placeCardOnTable(tmp)
                        playerOneHand.removeAt(cardPlayed - 1)
//                    }
                    //-------------------------


                } else {
                    if (playerTwoHand.isEmpty()) {
                        playerTwoHand = getCards(6)
                    }
                    cardPlayed = (1..playerTwoHand.size).random()

                    //-------------------------
//                    val handWinner = tallyUpScore(player, cardsOnTable[cardsOnTable.lastIndex], playerTwoHand[cardPlayed - 1])
//                    if (handWinner){
//                        displayTallyBoard(player)
//                    } else {
                        var tmp = mutableListOf<String>(playerTwoHand[cardPlayed - 1])
                        placeCardOnTable(tmp)
                        println("Computer plays ${playerTwoHand[cardPlayed - 1]}")
                        playerTwoHand.removeAt(cardPlayed - 1)
//                    }
                    //-------------------------

                }
                player = if (player == 0) 1 else 0

            } while (true)

            var cardsOnHandCnt = 0
            var cardsOnDeckCnt = 0

            println("Choose an action (reset, shuffle, get, exit):")
            val action = readln()
            when (action) {
                MENU.SHOW.action -> showDeck()
                "reset" -> dealCards()
                "shuffle" -> shuffleCards()
                "get" -> getTopCard()
                "exit" -> endGame()
                else -> println("Wrong action.")
            }
        } while (action != "exit")
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
        //println(workingDeck)
        val x = mutableListOf<String>()
        for (i in 0 until num) {
            x.add(workingDeck.get(workingDeck.lastIndex))
            workingDeck.removeAt(workingDeck.lastIndex)
        }
        //println(workingDeck)
        //println(x)
        return x
    }

    fun placeCardOnTable(deal: MutableList<String>) {
        for (n in deal){
            cardsOnTable.add(n)
        }
    }

    fun displayTallyBoard(player: Int) {
        if (player == 0){
            println("Player wins cards")
            playerOneHand += cardsOnTable
            playerOneWins++
        } else {
            println("Computer wins cards")
            playerTwoHand += cardsOnTable
            playerTwoWins++
        }
        cardsOnTable.clear()
        println("Score: Player: $playerOneWins - Computer $playerTwoWins")
        println("Cards: Player: ${playerOneHand.size} - Computer ${playerTwoHand.size}")
        //println("")
    }

    fun tallyUpScore(player: Int, topCard: String, playedCard: String): Boolean {
        val rankTopCard = topCard[0]
        val suitTopCard = topCard[1]

        val rankPlayedCard = playedCard[0]
        val suitPlayedCard = playedCard[1]

        if (rankTopCard == rankPlayedCard || suitTopCard == suitPlayedCard) {
            //println("we have a winner")
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

    fun endGame() {
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
// 281