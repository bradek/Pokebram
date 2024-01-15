# Pokebram
A Pokedex developped by Bram Dekeyser in Android Studio, using the Pokeapi.
Pokémon nor the Pokeapi are property of Bram Dekeyser, and are only used in order to make his own pokedex.
The code is made by him, with the help of some tools, dependencies and documentation.

## Content
* REST API fetching information in the form of a list.
* Display of the list, done through the usage of a recycler view.
* A searchview, checking if the pokémon name contains certain characters. (Checking by a query.)
* Clickable list items, leading to the correct details page.
* Fully detailed made details page with a link back to the main activity.
* Usage of images and graphbars on the details page to make the page more visual.

* _!!!Local data storing has been attempted, models and dao's have been made, though the application is still not working without internet connection.!!!_

## Sources for information and documentation
* [Android Documentation](https://developer.android.com/docs "Android docs")<br />
_For overall Java documentation, android information and Room database info._
* [Square Github](https://square.github.io/retrofit/ "Retrofit Documentation")<br />
_For documentation on how to use retrofit, a dependency whom makes it easier to use JSON._
* [PokéApi Documentation](https://pokeapi.co/docs/v2 "PokéApi Documentation")<br />
_For a better understanding about the possibilities of PokéApi. It came in handy for the information regarding pagination._

## AI Tools
* `[Github Copilot](https://github.com/features/copilot "Github Copilot")<br />
_I used Github Copilot for generating some of the models and DAO's (Database Objects.)_
_I also used Github Copilot for fixing some errors and issues and to detect irregularities._
_Most of the logging and error handling has also been generated._
_Github Copilot also helped in understanding the way the JSON of the PokeApi is structured, which is why I had no need to use Postman._
_Github Copilot chat doesn't save their conversations like ChatGPT, so I can't provide the specific prompts._
_This is why I try to be clear in this section on what I've used it._
