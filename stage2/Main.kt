// Stage 2/5: Virtual card deck
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

    init {
    	initializeGame()
    }

    fun chooseAction() {
        do {
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
        println("Card deck is reset.")
    }
    fun shuffleCards() {
        workingDeck.shuffle()
        println("Card deck is shuffled.")
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
        println("Bye")
        exitProcess(0)
    }
}


fun main() {
    val gameObj = IndigoGame()
    //gameObj.setupDeck()
    gameObj.chooseAction()
}
// 162 114 118 164