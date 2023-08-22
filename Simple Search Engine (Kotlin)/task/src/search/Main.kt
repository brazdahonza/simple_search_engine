package search

import java.io.File
import java.io.FileNotFoundException

fun main(args: Array<String>) {
//    var fileName = "/Users/brazdahonza/IdeaProjects/projekty/Simple Search Engine (Kotlin)/Simple Search Engine (Kotlin)/task/src/search/"
    var fileName = ""
    var mapOfTerms: Map<String, MutableList<Int>> = mutableMapOf()
    val listOfPeople: MutableList<String> = mutableListOf<String>()

    for(i in 0..args.lastIndex){
        if(args[i] == "--data") {
            fileName += args[i+1]
        }
    }

    try {
        val file = File(fileName)
        val lines = file.readLines()
        for(line in lines) {
            listOfPeople.add(line)
        }
        mapOfTerms = processData(lines)
    } catch (e: FileNotFoundException) {
        println(e.stackTrace)
    }

    while(true) {
        println("=== Menu ===")
        println("1. Find a person")
        println("2. Print all people")
        println("0. Exit")
        val option = readln()

        when(option) {
            "1" -> findPerson(listOfPeople, mapOfTerms)
            "2" -> printAllPeople(listOfPeople)
            "0" -> {
                println("Bye")
                break
            }
            else -> {
                println("Incorrect option! Try again.")
                continue
            }
        }
    }
}

fun processData(lines: List<String>): Map<String, MutableList<Int>> {
    val mapOfTerms: MutableMap<String, MutableList<Int>> = mutableMapOf<String, MutableList<Int>>()
    for ((index, line) in lines.withIndex()) {
        for (term in line.split(" ")) {
            val lineNums = mapOfTerms.getOrPut(term.lowercase()) { mutableListOf() }
            if (!lineNums.contains(index)) {
                lineNums.add(index)
            }
        }
    }
    return mapOfTerms
}


fun findPerson(listOfPeople: MutableList<String>, mapOfTerms: Map<String, MutableList<Int>>) {
    println("Select a matching strategy: ALL, ANY, NONE")
    val strategy = readln()
    val chosenStrategy = Strategy.valueOf(strategy)
    println("Enter a name or email to search all suitable people.")
    val query = readln().lowercase().split(" ")
    val returnedResults = searchForPerson(query, chosenStrategy, listOfPeople, mapOfTerms)

    if(returnedResults.isNotEmpty()) {
        for (result in returnedResults) {
            println(result)
        }
    } else {
        println("No matching people found.")
    }
    println()
}

fun printAllPeople(listOfPeople: MutableList<String>) {
    println("=== List of people ===")
    for(person in listOfPeople) {
        println(person)
    }
    println()
}

fun searchForPerson(
    query: List<String>,
    strategy: Strategy,
    listOfPeople: MutableList<String>,
    mapOfTerms: Map<String, MutableList<Int>>
): MutableList<String> {

    var listToReturn: MutableList<String> = mutableListOf()
    when(strategy) {
        Strategy.ANY -> listToReturn = findAny(query, listOfPeople, mapOfTerms)
        Strategy.ALL -> listToReturn = findAll(query, listOfPeople, mapOfTerms)
        Strategy.NONE -> listToReturn = findNone(query, listOfPeople, mapOfTerms)
    }



    return listToReturn
}

fun findAny(query: List<String>, listOfPeople: MutableList<String>, mapOfTerms: Map<String, MutableList<Int>>): MutableList<String> {
    var listToReturn: MutableList<String> = mutableListOf()

    for(term in query) {
        if (mapOfTerms.containsKey(term)) {
            for (value in mapOfTerms[term]!!) {
                if(!listToReturn.contains(listOfPeople[value]))  listToReturn.add(listOfPeople[value])
            }
        }
    }

    return listToReturn
}

fun findAll(queries: List<String>, listOfPeople: MutableList<String>, mapOfTerms: Map<String, MutableList<Int>>): MutableList<String> {
    var listToReturn: MutableList<String> = mutableListOf()

    for (person in listOfPeople){
        var contiansAll = true
        for(query in queries) {
            if(!person.lowercase().contains(query)) contiansAll = false
        }
        if (contiansAll) listToReturn.add(person)
    }

    return listToReturn
}

fun findNone(queries: List<String>, listOfPeople: MutableList<String>, mapOfTerms: Map<String, MutableList<Int>>): MutableList<String> {
    var listToReturn: MutableList<String> = mutableListOf()

    for (person in listOfPeople){
        var contiansNone = true
        for(query in queries) {
            if(person.lowercase().contains(query)) contiansNone = false
        }
        if (contiansNone) listToReturn.add(person)
    }

    return listToReturn
}


enum class Strategy(s: String) {
    ANY("ANY"),
    ALL("ALL"),
    NONE("NONE")
}


