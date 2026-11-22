import kotlin.random.Random

fun printField(player: Player, hideShips: Boolean) {
    print("  ")
    for (c in 0..9) print("$c ")
    println()
    for (r in 0..9) {
        print("$r ")
        for (c in 0..9) {
            val state = player.field()[r][c]
            val shown = if (hideShips && state == CellState.SHIP) CellState.EMPTY else state
            print("${shown.toChar()} ")
        }
        println()
    }
}

fun printBothFields(player: Player, enemy: Player) {
    println("=== Поле игрока ${player.name} ===       === Поле игрока ${enemy.name} ===")
    print("  ")
    for (c in 0..9) print("$c ")
    print("       ")
    print("  ")
    for (c in 0..9) print("$c ")
    println()

    for (r in 0..9) {
        print("$r ")
        for (c in 0..9) print("${player.field()[r][c].toChar()} ")
        print("       ")
        print("$r ")
        for (c in 0..9) {
            val state = enemy.field()[r][c]
            val shown = if (state == CellState.SHIP) CellState.EMPTY else state
            print("${shown.toChar()} ")
        }
        println()
    }
}

fun printRules() {
    println("==========================================")
    println("          МОРСКОЙ БОЙ — ПРАВИЛА")
    println("==========================================")
    println("Поле 10x10. Координаты: сначала строка (0-9), потом столбец (0-9).")
    println()
    println("Корабли, которые нужно расставить:")
    println("  1 корабль  x 4 палубы")
    println("  2 корабля  x 3 палубы")
    println("  3 корабля  x 2 палубы")
    println("  4 корабля  x 1 палуба")
    println("Итого: 10 кораблей, 20 палуб.")
    println()
    println("Правила расстановки:")
    println("  * Корабли не могут выходить за границы поля.")
    println("  * Корабли не могут пересекаться.")
    println("  * Корабли не могут касаться друг друга даже углами.")
    println("==========================================")
    println()
}

fun placePlayerShips(player: Player) {
    val plan = listOf(4, 3, 3, 2, 2, 2, 1, 1, 1, 1)
    var index = 0

    while (index < plan.size) {
        val expectedSize = plan[index]
        println()
        println("Ставим корабль #${index + 1} из ${plan.size} ($expectedSize палуб).")
        printField(player, false)

        println("Введите начальную координату (row col):")
        val input = readln().trim().split(" ")
        if (input.size != 2) {
            println("Ошибка: нужно два числа через пробел.")
            continue
        }
        val startRow = input[0].toIntOrNull()
        val startCol = input[1].toIntOrNull()
        if (startRow == null || startCol == null) {
            println("Ошибка: координаты должны быть числами.")
            continue
        }

        println("Введите тип корабля (1, 2, 3 или 4):")
        val size = readln().trim().toIntOrNull()
        if (size == null || size !in 1..4) {
            println("Ошибка: тип корабля должен быть 1, 2, 3 или 4.")
            continue
        }
        if (size != expectedSize) {
            println("Сейчас нужно ставить корабль из $expectedSize палуб, а не $size.")
            continue
        }

        println("Введите направление (h — вправо, v — вниз):")
        val dir = readln().trim().lowercase()
        if (dir != "h" && dir != "v") {
            println("Ошибка: направление должно быть h или v.")
            continue
        }
        val horizontal = (dir == "h")

        val ok = player.placeShip(startRow, startCol, size, horizontal)
        if (ok) {
            println("Корабль поставлен!")
            index++
        } else {
            println("Нельзя поставить корабль здесь. Попробуйте снова.")
        }
    }
    println()
    println("Все корабли расставлены!")
    printField(player, false)
}

fun placeEnemyShips(enemy: Player) {
    val plan = listOf(4, 3, 3, 2, 2, 2, 1, 1, 1, 1)
    val maxAttempts = 1000

    var attempt = 0
    var index = 0
    while (index < plan.size) {
        val size = plan[index]
        val startRow = Random.nextInt(0, 10)
        val startCol = Random.nextInt(0, 10)
        val horizontal = Random.nextBoolean()

        val ok = enemy.placeShip(startRow, startCol, size, horizontal)
        if (ok) {
            index++
            attempt = 0
        } else {
            attempt++
            if (attempt > maxAttempts) {
                println("Компьютер не смог расставить корабли. Сбрасываем поле...")
                enemy.reset()
                index = 0
                attempt = 0
            }
        }
    }
    println("Компьютер расставил корабли.")
}

// фиксированная расстановка для отладки
fun placeEnemyShipsTemplate(enemy: Player) {
    enemy.placeShip(0, 0, 4, true)  // 4-палубный
    enemy.placeShip(2, 0, 3, true)  // 3-палубный
    enemy.placeShip(2, 5, 3, true)  // 3-палубный
    enemy.placeShip(4, 0, 2, true)  // 2-палубный
    enemy.placeShip(4, 3, 2, true)  // 2-палубный
    enemy.placeShip(4, 6, 2, true)  // 2-палубный
    enemy.placeShip(6, 0, 1, true)  // 1-палубный
    enemy.placeShip(6, 2, 1, true)  // 1-палубный
    enemy.placeShip(6, 4, 1, true)  // 1-палубный
    enemy.placeShip(6, 6, 1, true)  // 1-палубный
    println("Компьютер расставил корабли по шаблону.")
}

fun main() {
    val player = Player("Вы")
    val enemy = Player("Компьютер")

    printRules()

    println("=== РАССТАНОВКА ВАШИХ КОРАБЛЕЙ ===")
    placePlayerShips(player)

    println()
    println("=== КОМПЬЮТЕР РАССТАВЛЯЕТ КОРАБЛИ ===")

    placeEnemyShips(enemy)

    println()
    println("=== ИГРА НАЧАЛАСЬ ===")
    println("(Для выхода из игры введите quit)")

    var playerTurn = true
    var moves = 0
    var exited = false

    while (player.hasShips() && enemy.hasShips()) {
        printBothFields(player, enemy)
        println("Кораблей осталось — у вас: ${player.countShips()}, у врага: ${enemy.countShips()} (потоплено врагом: ${player.sunkShips()}, вами: ${enemy.sunkShips()})")
        println()

        if (playerTurn) {
            println("Ваш ход. Введите координаты выстрела (row col) или quit:")
            val rawInput = readln().trim()

            if (rawInput.equals("quit", ignoreCase = true)) {
                println("Вы вышли из игры.")
                exited = true
                break
            }

            val input = rawInput.split(" ")
            if (input.size != 2) {
                println("Ошибка: нужно два числа через пробел.")
                continue
            }
            val r = input[0].toIntOrNull()
            val c = input[1].toIntOrNull()
            if (r == null || c == null) {
                println("Ошибка: координаты должны быть числами.")
                continue
            }

            if (!enemy.isInside(r, c)) {
                println("Ошибка: координаты за границей поля (нужно от 0 до 9). Попробуйте снова.")
                continue
            }
            val targetState = enemy.field()[r][c]
            if (targetState == CellState.HIT || targetState == CellState.MISS) {
                println("Ошибка: вы уже стреляли в эту клетку! Выберите другую.")
                continue
            }

            moves++
            val hit = enemy.shoot(r, c)
            if (hit) {
                println("Попадание!")
            } else {
                println("Промах.")
                playerTurn = false
            }
        } else {
            println("Ход компьютера...")
            var r: Int
            var c: Int
            do {
                r = Random.nextInt(0, 10)
                c = Random.nextInt(0, 10)
            } while (player.field()[r][c] == CellState.HIT || player.field()[r][c] == CellState.MISS)

            moves++
            val hit = player.shoot(r, c)
            if (hit) {
                println("Компьютер попал в ($r, $c)!")
            } else {
                println("Компьютер промахнулся в ($r, $c).")
                playerTurn = true
            }
        }
        println()
    }
    if (!exited) {
        if (enemy.hasShips()) {
            println("Победил компьютер!")
        } else {
            println("Вы победили!")
        }
    }
    println("Игра длилась $moves ходов.")
}
