# PC Builder
Cross-platform java app which loads in and displays premade pc config profiles, from either a downloaded zip file or a url which points to a zip file download

This is just a project I made for fun, because I wanted to help my friend build a PC and I thought why not make an app for it.

Feel free to open issues, i might solve them, although I cant promise that they will be fixed

#### Requirements: Java (version 8 and up)
#### Tested with: Java 8 and 10 on Windows & MacOS

### Current features:
* **Native look** depending on OS
* Display components with defined images, names, and specs
* **3 types of links**
  * clicking on brand name or model number opens up the **product site**
  * clicking on price opens up the **configured shop** site that the price originates from
  * left clicking on image opens up the **price site** of that product which lists all available shops to buy from
* **Price sum calculation**, customizable **price prefix/suffix** and support for double price values
  * The price of a product is calculated into the sum if the category is enabled at the top and is the selected product in that category
* **Image enlarge and browse with arrow keys**
* Profile import from **different sources (folder, zip, url)**, profile management & reload
* Welcome message with changelog when a new version of the app is opened
* Keyboard shortcuts (explained in the Help dialog)

### Planned features (if I decide to continue this project in the future):
* **Profile creator** - currently the profiles need to be written manually in xml
* **Universal product properties and components** - currently all extra properties below a product and extra categories need to be implemented in Java classes in order to be displayed
* Support for **more than one shop + price comparisons**
* Support for **dynamic product prices** - fetching the price from a price site (this would require me to develop html info extractors for all price sites I want to support)

The config profiles have to be made manually, following the structure found here inside the Resources folder. (there are some example profiles in there) If I have the mood I might make a tutorial here about making profiles

### Gallery
 * Help dialog
 
![Help dialog](Resources/screenshots/Screen%20Shot%202018-07-01%20at%2011.07.54.png)

 * MacOS
![Windows Welcome dialog](Resources/screenshots/Screen%20Shot%202018-07-01%20at%2011.53.58.png)

 * MacOS Maximized
![Windows Welcome dialog](Resources/screenshots/Screen%20Shot%202018-07-05%20at%2011.32.04.png)

 * Windows Maximized
![Windows Maximized](Resources/screenshots/2019-01-27%20(3).png)

 * Windows Welcome dialog
![Windows Welcome dialog](Resources/screenshots/2019-01-27%20(8).png)

More screenshots can be found in Resources/screenshots
