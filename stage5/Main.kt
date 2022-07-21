// start C:\Users\lixot\IdeaProjects\Indigo Card Game\Indigo Card Game\task\src\indigo
// Stage Stage 5/5: Player VS Computer
package indigo

import kotlin.system.exitProcess

enum class MENU(val action: String) {
    SHOW("show"),
    RESET("get"),
    SHUFFLE("shuffle"),
    GET("get"),
    EXIT("exit"),
}

const val CARD_PER_DECK = 52

class Board : DeckV2() {

    init {
        cards.clear()
    }
}

class PlayerGuy : DeckV2() {
    var name = ""
    var cardsWon = mutableListOf<Card>() // tracking cards won
    var winCnt = 0

    init {
        cards.clear()
    }

    fun updateTally() {
        winCnt = tallyRoundScore()
    }

    fun tallyRoundScore() :Int {
        var cnt = 0

        for (n in cardsWon.indices) cnt += if (cardsWon[n].rank in listOf("A", "10", "J", "Q", "K")) 1 else 0

        return cnt
    }
}


open class DeckV2 {
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

    var cards = mutableListOf<Card>()

    init {
        for (s in SUITS.values()) {
            for (r in RANKS.values()) {
                cards.add(Card(r.rank, s.suit))
            }
            cards.toList()
        }
    }

    fun dealCards(num:Int): MutableList<Card> {
        if (cards.isEmpty()) {
            return mutableListOf()
        }
        val x = mutableListOf<Card>()

        for (i in 0 until num) {
            x.add(cards.last())
            //x.add(cardDeck.get(cardDeck.lastIndex))
            cards.removeAt(cards.lastIndex)
        }

        return x
    }

    fun showCards(numMenu: Boolean = true): String {
        var tmp = ""
        if (numMenu) {
            for (i in cards.indices){
                tmp += "${i + 1})${cards[i]} "
            }
        } else {
            for (i in cards){
                tmp += "$i "
            }
        }

        return tmp.trim()
    }
}


data class Card(val rank: String, val suit: String) {
    override fun toString(): String {
        return "$rank$suit"
    }
    override fun equals(other: Any?): Boolean {
        (other as Card).also { return other.rank == rank || other.suit == suit }
    }
}


class IndigoGame{
    var cardDeckV2 = DeckV2()
    val table = Board()
    val playerOne = PlayerGuy()
    val playerTwo = PlayerGuy()

    var debugFlag1 = ""
    var debugOn = false

    var totalCardsLeftCnt = CARD_PER_DECK
    var totalCardsTakenCnt = 0

    var lastHandWinner = 9

    init {
        playerOne.name = "Player One"
        playerTwo.name = "Player Two/Computer"
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
        table.cards =  cardDeckV2.dealCards(4)
        playerOne.cards =  cardDeckV2.dealCards(6)
        playerTwo.cards =  cardDeckV2.dealCards(6)

        println(table.showCards(false))
    }

    fun initialGameVariable(player: Boolean) {
        // Initialize this for player who played first card. RARE,in case no player wins a card.
        lastHandWinner = if (player) 1 else 2
    }

    fun chooseAction() {
        do {
            playGame()

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

        var cardSelected = 0
        var cardPlayed = Card("x", "x")
        var topCard: Card =  Card("w", "joker")

        do {
            if (table.cards.isEmpty()) {
                println("\nNo cards on the table")
                topCard =  Card("", "")
            } else {
                topCard = table.cards.last()
                println("\n${table.cards.size} cards on the table, and the top card is ${table.cards.last()}")
            }

            if (table.cards.size == 52) {
                endGame()
            }

            if (firstPlayer) {
                if (playerOne.cards.isEmpty()) {
                    playerOne.cards = cardDeckV2.dealCards(6)
                    if (playerOne.cards.isEmpty()) wrapUpNomorecards(firstPlayer)
                }

                print("Cards in hand: ")
                println(playerOne.showCards())

                do{
                    println("Choose a card to play (1-${playerOne.cards.size}):")
                    cardSelected = 0
                    val tmp = readln()

                    if (tmp == "exit") endGame()

                    val myRegex2 = Regex("[1-${playerOne.cards.size}]")
                    if (myRegex2.matches(tmp)) {
                        cardSelected = tmp.toInt()
                        cardPlayed = playerOne.cards[cardSelected - 1]
                    }
                } while (cardSelected !in 1..playerOne.cards.size)
            } else {
                if (playerTwo.cards.isEmpty()) {
                    playerTwo.cards = cardDeckV2.dealCards(6)
                    if (playerTwo.cards.isEmpty()) {
                        wrapUpNomorecards(firstPlayer)
                    }
                }

                println(playerTwo.showCards(false))
                // println(playerTwo.showCards() + "1111111111111111111111111")//
                //cardSelected = (1..playerTwo.cards.size).random()
                cardSelected = playerResponse(playerTwo, topCard)

                cardPlayed =  playerTwo.cards[cardSelected - 1]

                println("Computer plays ${playerTwo.cards[cardSelected - 1]}")
            }
            val handWinner = analyzeHandForWin(cardPlayed)

            placeCardOnTable(cardPlayed)
            removeHandCard(firstPlayer, cardSelected - 1)

            // A bit of clean up on tallies
            if (handWinner) {
                lastHandWinner = if (firstPlayer) 1 else 2
            }

            if (handWinner){
                if (firstPlayer) {
                    playerOne.cardsWon.addAll(table.cards)
                    playerOne.updateTally()
                } else {
                    playerTwo.cardsWon.addAll(table.cards)
                    playerTwo.updateTally()
                }
                table.cards.clear()

                displayTallyBoard(firstPlayer)
            }

            firstPlayer = !firstPlayer
        } while (true)
    }

    fun playerResponse (player: PlayerGuy, topCard: Card) : Int {

        var resp = (1..player.cards.size).random()

        var sameSuitCnt = 0
        var sameRankCnt = 0
        val sameSuitCards = mutableListOf<Int>()
        val sameRankCards = mutableListOf<Int>()

        val suitArr = listOf<String>(DeckV2.SUITS.DIAMONDS.suit,
            DeckV2.SUITS.HEARTS.suit,
            DeckV2.SUITS.SPADES.suit,
            DeckV2.SUITS.CLUBS.suit)

        //val debug = false
        debugFlag1 = ""

        if (debugOn) {
            //println("table cards:  " + table.cards)
            //println("Computer cards" + player.cards)

            //print("Hand Card Size: " + player.cards.size)
            //println(" Table Cards Size: " + table.cards.size)
        }

        // Scenarion #1
        if (player.cards.size == 1){
            val tossNum = 1
            val tossCard = playerTwo.cards[tossNum - 1]

            // debugFlag1 = "1)$tossCard Only 1 card in hand, toss it"
            printToss(debugFlag1)
            return tossNum
        } else {

            // Scenarion #3
            if (table.cards.isEmpty()){
                return noCardOnTableOrNoCandidates(player, topCard, "3")
            }

            // Candidate Check
            // Scenarion #5
            for (p in player.cards.indices) {
                if (player.cards[p].equals(topCard)){
                    if (player.cards[p].suit == topCard.suit) {
                        sameSuitCnt++
                        sameSuitCards.add(p)
                    }
                    if (player.cards[p].rank == topCard.rank) {
                        sameRankCnt++
                        sameRankCards.add(p)
                    }
                }
            } // end For

            val sameCnt = sameSuitCnt + sameRankCnt

            // Scenarion #2
            if (sameCnt == 1) {
                val tmp = sameSuitCards + sameRankCards

                val tossNum = tmp.random() + 1
                val tossCard = playerTwo.cards[tossNum - 1]

                // debugFlag1 = "2)$tossCard JUST 1 Candidate SUIT/RANK match. TOPCARD $topCard"
                // printToss(debugFlag1)
                return tossNum
            }
            //--------------------------------------------------

            // Scenarion #4- NO Candidates
            if (sameCnt == 0){
                return noCardOnTableOrNoCandidates(player, topCard, "4")
            }
            //--------------------------------------------------

            // Scenarion #5
            if (sameCnt >= 2) {
                if (sameSuitCnt >= 2) {                     // a
                    val tossNum = sameSuitCards.random() + 1
                    val tossCard = playerTwo.cards[tossNum - 1]

                    // debugFlag1 = "5-a)$tossCard 2 or more SUIT match TOPCARD $topCard"
                    // printToss(debugFlag1)
                    return tossNum
                } else if (sameRankCnt >= 2) {              // b
                    val tossNum = sameRankCards.random() + 1
                    val tossCard = playerTwo.cards[tossNum - 1]

                    // debugFlag1 = "5-b)$tossCard 2 or more RANK match TOPCARD $topCard"
                    // printToss(debugFlag1)
                    return tossNum
                } else {                                    // c
                    val tossNum = (sameSuitCards + sameRankCards).random() + 1
                    val tossCard = playerTwo.cards[tossNum - 1]

                    // debugFlag1 = "5-c)$tossCard NOT 2 or more SUIT/RANK match TOPCARD $topCard"
                    // printToss(debugFlag1)
                    return tossNum
                }
            }
            //--------------------------------------------------
        }

        error("this line should never b reached...")
        //return resp
    }

    fun printToss(msg: String = "") {
        if (debugOn && msg != "") println("============= $msg =============")
    }

    fun noCardOnTableOrNoCandidates (player: PlayerGuy, topCard: Card, magic: String) :Int {
        for (p1 in player.cards.indices) {
            for (p2 in player.cards.indices) {
                if (player.cards[p1].suit == player.cards[p2].suit) {
                    if (p1 == p2) continue

                    val tossNum = p1 + 1
                    val tossCard = playerTwo.cards[tossNum - 1]

                    if (magic == "3") {
                        // debugFlag1 = "3-a)$tossCard Empty Table with Hand SUIT match. >> $topCard"
                        // printToss(debugFlag1)
                        return tossNum
                    }
                    else {
                        // debugFlag1 = "4-a)$tossCard No Candidate with Hand SUIT match. >> $topCard"
                        // printToss(debugFlag1)
                        return tossNum
                    }
                }
            }
        }
        for (p1 in player.cards.indices) {
            for (p2 in player.cards.indices) {
                if (player.cards[p1].rank == player.cards[p2].rank) {
                    if (p1 == p2) continue

                    val tossNum = p1 + 1
                    val tossCard = playerTwo.cards[tossNum - 1]

                    if (magic == "3") {
                        // debugFlag1 = "3-b)$tossCard Empty Table with Hand RANK match. >> $topCard"
                        // printToss(debugFlag1)
                        return tossNum
                    }
                    else{
                        // debugFlag1 = "4-b)$tossCard No Candidate with Hand RANK match. >> $topCard"
                        // printToss(debugFlag1)
                        return tossNum
                    }
                }
            }
        }
        val tossNum = player.cards.indices.random() + 1
        val tossCard = playerTwo.cards[tossNum - 1]

        if (magic == "3") {
            // debugFlag1 = "3-c)$tossCard Empty Table with Hand NO SUIT/RANK toss any card >> $topCard"
            // printToss(debugFlag1)
            return tossNum
        }
        else {
            // debugFlag1 = "4-c)$tossCard No Candidate with NO SUIT/RANK...toss any card TOPCARD $topCard"
            // printToss(debugFlag1)
            return tossNum
        }
    }


    fun wrapUpNomorecards(player: Boolean) {
        awardRemainingCards()
        //updateTally(player)
        displayTallyBoard(player, true)
        endGame()
        endGame("""
                    empty because no more cards in working deck
                    -- these cards are Left on table
                    -- \n${table.cards}
                    """)
    }

    fun initializeGame() {
    }

    fun placeCardOnTable(card: Card) {
        table.cards.add(card)
    }

    /**
     * DO NOT use Card to remove, you must used the index.
     * The EQUAL OVERRIDE in the data class causes it to match and remove other cards
     * due to rank ot suit will be a match
     */
    fun removeHandCard(player: Boolean, cardIndex: Int) {
        if (player) playerOne.cards.removeAt(cardIndex)else playerTwo.cards.removeAt(cardIndex)
    }


    fun displayTallyBoard(firstPlayer: Boolean, end: Boolean = false) {
        if (!end) {
            if (firstPlayer){
                println("Player wins cards")
            } else {
                println("Computer wins cards")
            }
        }
        println("Score: Player ${playerOne.winCnt} - Computer ${playerTwo.winCnt}")
        println("Cards: Player ${playerOne.cardsWon.size} - Computer ${playerTwo.cardsWon.size}")
    }


    fun awardRemainingCards() {
        if (lastHandWinner == 1){
            playerOne.cardsWon += table.cards
            playerOne.winCnt = playerOne.tallyRoundScore()
        } else {
            playerTwo.cardsWon += table.cards
            playerTwo.winCnt = playerTwo.tallyRoundScore()
        }

        if (playerOne.cardsWon.size > playerTwo.cardsWon.size) {
            playerOne.winCnt += 3
        } else {
            playerTwo.winCnt += 3
        }

        table.cards.clear()
    }


    fun analyzeHandForWin(playedCard: Card): Boolean {
        if (table.cards.isEmpty()) return false

        return table.cards.last().equals(playedCard)
    }


    fun showDeck() {
        var cnt = 0
        for (idx in 0 until cardDeckV2.cards.size) {
            cnt++
            if (cnt == 14 ) {
                print("\n" +cardDeckV2.cards[idx]+"  ")
                cnt = 1
            } else {
                print(cardDeckV2.cards[idx])
            }
        }
        println("\n=================================================================================================")
        cnt = 0
        for (idx in 0 until cardDeckV2.cards.size) {
            cnt++
            if (cnt == 14 ) {
                print("\n" +cardDeckV2.cards[idx]+"  ")
                cnt = 1
            } else {
                print(cardDeckV2.cards[idx])
            }
        }
        println("\n=================================================================================================")
    }

    fun dealCards() {
        totalCardsLeftCnt = CARD_PER_DECK
        totalCardsTakenCnt = 0
        //println("Card deck is reset.")
    }

    fun shuffleCards() {
        cardDeckV2.cards.shuffle()
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
                print("${cardDeckV2.cards[cnt]} ")
                cardDeckV2.cards.removeAt(cnt)
            }
            totalCardsTakenCnt += num
            totalCardsLeftCnt -= num

            println()
        }

        chooseAction()
        return
    }

    fun endGame(msg: String = "") {
        println(if (msg != "") msg else "Game Over")
        exitProcess(0)
    }
}


fun main() {
    val gameObj = IndigoGame()
    //gameObj.setupDeck()
    gameObj.chooseAction()
}
// 617 434 446 441 453 482 468 351 281