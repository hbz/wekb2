# Install all the dependencies on your pc

## Install nodejs on Linux

    sudo apt-get install --yes nodejs

## Install Node.js on Windows with NVM (Node version manager for Windows) to manage Node.js versions

1.) go to https://github.com/coreybutler/nvm-windows/releases and download nvm-setup.exe

2.) install NVM (Node version manager for Windows) on Windows via installation file

3.) restart windows before next step

4.) install the latest Node.js via NVM version


    nvm install lts

5.) list all the installed node versions with

    nvm list

6.) specify the node version you would like to use e.g. 18.16.0

    nvm use  20.10.0



# Install Fomantic UI for the first time

- change the package.json to the new fomantic ui version
- change the semantic.json to the new fomantic ui version
- check if the needed node version is installed on your pc with 'nvm current'
- go to the folder 'files\frontend'


    npm install --ignore-scripts fomantic-ui
    npm update
    cd node_modules/fomantic-ui
    npx gulp install


## Install gulp globaly

    npm install -g gulp

## Delete themes you do not need

- only the themes 'default' and 'wekb' should remain in 'files/frontend/semantic/src/themes'

## Pay attention to new files from the Fomantic UI update

- add them to git repository

## Build the semantic.min.css and semantic.min.js

- go to folder 'frontend\semantic'


     gulp build

# Update Fomantic UI

1. Change to folder 'frontend'

   cd C:\Users\xxx\IdeaProjects\yyy\files\frontend

2. Installing new fomantic version

For Version 2.9.4:

    npm install fomantic-ui@2.9.4

This makes the new version available here:

    frontend\node_modules\fomantic-ui\

The files in the folder 'frontend\semantic\src' are not touched by this, only files/frontend/package.json and files/frontend/package-lock.json

3. Secure your own adaptations


4. Copy the new files from node_modules\fomantic-ui to our framework stucture under files/frontend/semantic


    Copy-Item .\node_modules\fomantic-ui\tasks\* .\semantic\tasks\ -Recurse -Force
    Copy-Item .\node_modules\fomantic-ui\src\* .\semantic\src -Recurse -Force
    Copy-Item .\node_modules\fomantic-ui\gulpfile.js .\semantic\gulpfile.js -Force

5. Watch out for new structure after update, delete or merge old files, delete all the other themes you do not need.


6. Build css


    cd semantic
    gulp build


# How to customize CSS

Our custom themes and the default theme override the *.less in 'src/definitions'. The folder 'src/side' is not used yet.


## Theming

- we got the themes 'laser' and 'accessibility'
- all changes in css we make by changing inside the theme!
- the original semantic ui file for gulp bilding 'app/semantic/tasks/build.js' is changed in order to build two themes at the same time (laser & accessibility)
- meanwile the gulp build process temp files are builded and moved around

## Example

I would like to change the padding between an icon and content in a list

1.) find the variable in default theme. In this case there

    src/themes/default/elements/list.variables

2.) create the list.variables in the laser theme folder

    src/themes/laser/elements/list.variables

3.) change the specifig variable only there

4.) Change the theme.config

    /files/frontend/semantic/src/definitions/themes/laser/theme.config

    /files/frontend/semantic/src/definitions/themes/accessibility/theme.config

old:

    @list       : 'default';

new:

    @list       : 'laser';

5.) Build css

    cd semantic
    gulp build

