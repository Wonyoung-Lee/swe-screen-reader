//Global Variables
let ALL_ELEMENTS = [] // Contains all the elements
let PAGE_MAP = {} // A mapping of elements to i
let utterThis = new SpeechSynthesisUtterance("Welcome to the Screen Reader")
let CURRENT_ELEMENT = {
    // a function that updates this.value to newElement and reads the element
    setAndSpeak: (newElement) => {
        // Set element
        this.value = newElement
        console.log("this is new element" , newElement)
        // Speak if the element isn't null
        if(this.value !== null) {
            // Get current tag name
            let CURRENT_TAG = this.value.tagName

            console.log(CURRENT_TAG)
            // Call the correct method for the given tag name
            let CURRENT_CATEGORY = ROLES[CURRENT_TAG]
            // Get the function to handle speech for the current tag
            let SPEECH_HANDLER = HANDLERS[CURRENT_CATEGORY]
            // This makes sure the speech handler is a function like we want
            if(typeof SPEECH_HANDLER === 'function') {
                SPEECH_HANDLER(this.value)
            }
        }
    },
    value: null,
}

// Called when the window is loaded
window.onload = () => {
    // Maps page elements
    mapPage()
    addVoiceSlowDownBtn()
    addVoiceSpeedUpBtn()
    addBackwardBtn()
    addForwardBtn()
    addPlayPauseBtn()
    document.addEventListener('keyup', event => {
        //Starts the reader
        if (event.code === 'KeyP') {
            event.preventDefault();
            // Cycle through every element
            for (let i = 0; i < ALL_ELEMENTS.length; i++) {
                // Get current element using page map
                let newElement = document.getElementById(PAGE_MAP[i])
                // Speak the current element according to the handler
                CURRENT_ELEMENT.setAndSpeak(newElement)
            }
        }
        // Pauses and unpauses the reader
        if (event.code === 'KeyS') {
            event.preventDefault();
            window.speechSynthesis.pause()
            if(window.speechSynthesis.paused){
                window.speechSynthesis.resume()
            }
        }

        //TODO forwards and backwards
    })
}
// TODO injectHTML()

const addPlayPauseBtn = () => {
    let playPauseBtn = document.createElement("button");
    playPauseBtn.innerHTML = "Play/Pause";
    document.body.insertBefore(playPauseBtn, document.body.firstChild);
    playPauseBtn.addEventListener("click", event => {
        window.speechSynthesis.pause()
        if(window.speechSynthesis.paused) {
            window.speechSynthesis.resume()
        }
    })
}
const addForwardBtn = () => {
    let forwardBtn = document.createElement("button");
    forwardBtn.innerHTML = "Go Forward";
    document.body.insertBefore(forwardBtn, document.body.firstChild);
    forwardBtn.addEventListener("click", event => {
            //add forward method code
            console.log("I'm going forward!")
        }
    )
}
const addBackwardBtn = () => {
    let backwardBtn = document.createElement("button");
    backwardBtn.innerHTML = "Go Backward";
    document.body.insertBefore(backwardBtn, document.body.firstChild);
    backwardBtn.addEventListener("click", event => {
            //add backward method code
            console.log("I'm going backward!")
        }
    )
}
const addVoiceSpeedUpBtn = () => {
    let voiceSpeedUpBtn = document.createElement("button");
    voiceSpeedUpBtn.innerHTML = "Read Faster";
    document.body.insertBefore(voiceSpeedUpBtn,document.body.firstChild);
    voiceSpeedUpBtn.addEventListener("click", event => {
            utterThis.rate = utterThis.rate + 1
            console.log(utterThis.rate)
        }
    )
}

const addVoiceSlowDownBtn = () => {
    let voiceSlowDownBtn = document.createElement("button");
    voiceSlowDownBtn.innerHTML = "Read Slower";
    document.body.insertBefore(voiceSlowDownBtn,document.body.firstChild);
    voiceSlowDownBtn.addEventListener("click", event => {
            utterThis.rate = utterThis.rate - 1
            console.log(utterThis.rate)
        }
    )
}
const voiceOver = (textToSpeak) => {
    let voices = window.speechSynthesis.getVoices()
    utterThis = new SpeechSynthesisUtterance(textToSpeak)
    window.speechSynthesis.speak(utterThis)
    //
    return new Promise((resolve) => {
        utterThis.onend = () => resolve()
        // window.setInterval(() => {
        //     if(!window.speechSynthesis.speaking){
        //         resolve()
        //     }
        // }
        // ,250
        // )
    })
}
const mapPage = () => {
    // Get the elements
    if (ALL_ELEMENTS.length === 0){
        ALL_ELEMENTS = document.body.getElementsByTagName("*")
    }
    // Assign every element an id
    for (let i = 0; i < ALL_ELEMENTS.length; i++) {
        const currentElement = ALL_ELEMENTS[i]
        if (!currentElement.id){
            currentElement.id = i
        }
        PAGE_MAP[currentElement.id] = i
    }
}



// Handles tags that are in the metadata category
// TODO temporarily the same as the text handler
const metadataHandler = (currentElement) => {
    let textToSpeak = currentElement.innerText
    voiceOver(textToSpeak)
}

// Handles tags that are in the section category
const sectionHandler = (currentElement) => {
    let textToSpeak = "You are in the " + currentElement.tagName + "section"
    voiceOver(textToSpeak)
}

// Handles tags that are in the text category
const textHandler = async (currentElement) => {
    let textToSpeak = currentElement.innerText
    await voiceOver(textToSpeak)
}

// Handles tags that are in the metadata category
// TODO temporarily the same as the text handler
const groupsHandler = (currentElement) => {
    let textToSpeak = currentElement.innerText
    voiceOver(textToSpeak)
}

// Handles tags that are in the metadata category
// TODO temporarily the same as the text handler
const figuresHandler = (currentElement) => {
    let textToSpeak = currentElement.innerText
    voiceOver(textToSpeak)
}

// TODO temporarily the same as the text handler
const listHandler = (currentElement) => {
    let textToSpeak = currentElement.innerText
    voiceOver(textToSpeak)
}

// TODO temporarily the same as the text handler
// const interactiveHandler = (currentElement) => {
//     let textToSpeak = currentElement.innerText
//     voiceOver(textToSpeak)
// }

const linkHandler = async (currentElement) => {

    let textToSpeak = "this is a link" + currentElement.innerText
    await voiceOver(textToSpeak)
    let link = currentElement.href
    textToSpeak = "the link address is" + link
    await voiceOver(textToSpeak)
    await voiceOver("Would you like to open it in a new window? Press O to open link in new window. Press S to resume voice over")
    console.log(window.speechSynthesis.speaking)
    console.log(window.speechSynthesis.paused)
    window.speechSynthesis.pause()
    console.log("hi")
    document.addEventListener('keyup', event => {
        if (event.code === 'KeyO') {
            event.preventDefault()
            window.open(link,"_blank")
        }
    })
}

const buttonHandler = async (currentElement) => {
    let textToSpeak = "this is a button" + currentElement.innerText
    voiceOver(textToSpeak)
    textToSpeak = "Would you like to press the button? Press B to press the button. Press S to resume voice over"
    voiceOver(textToSpeak)
    document.addEventListener('keyup', event => {
        if (event.code === 'KeyB') {
            currentElement.click()
            console.log("you pressed the button!")
        }
    })
}

const inputHandler = async (currentElement) => {
    let textToSpeak = "this is an input box" + currentElement.innerText
    voiceOver(textToSpeak)
    textToSpeak = "click T to type in the box"
    voiceOver(textToSpeak)
    document.addEventListener('keyup', event => {
        if (event.code === 'KeyT') {
            currentElement.click()
            console.log("you can now type in text box!")
        }
    })

}

// TODO temporarily the same as the text handler
const tableHandler = async (currentElement) => {
    let textToSpeak = currentElement.innerText
    voiceOver(textToSpeak)
}

// TODO temporarily the same as the text handler
const multimediaHandler = async (currentElement) => {
    let textToSpeak = currentElement.innerText
    await voiceOver(textToSpeak)
}

// TODO temporarily the same as the text handler
const formHandler = (currentElement) => {
    let textToSpeak = currentElement.innerText
    voiceOver(textToSpeak)
}

// // TODO temporarily the same as the text handler
// const buttonHandler = (currentElement) => {
//     let textToSpeak = currentElement.getAttribute("href")
//     voiceOver(textToSpeak)
// }




// maps element category names to handler functions
const HANDLERS = {
    "metadata" : metadataHandler,
    "section" : sectionHandler,
    "text" : textHandler,
    "groups" : groupsHandler,
    "figures" : figuresHandler,
    "list" : listHandler,
    "link":linkHandler,
    "button": buttonHandler,
    "input":inputHandler,
    "table" : tableHandler,
    "multimedia" : multimediaHandler,
    "form" : formHandler,
    "button" : buttonHandler,
}

// maps element tag names to element categories
// element tag -> category
const ROLES = {
    "TITLE" : "metadata",

    "HEADER" : "section",
    "ASIDE" : "section",
    "ARTICLE" : "section",
    "FOOTER" : "section",
    "MAIN" : "section",
    "NAV" : "section",
    "SECTION" : "section",
    "LI" : "section",


    "P" : "text",
    "H1" : "text",
    "H2" : "text",
    "H3" : "text",
    "H4" : "text",
    "H5" : "text",
    "H6" : "text",

    "BLOCKQUOTE" : "groups",
    "FIGCAPTION" : "groups",
    "CITE" : "groups",
    "CAPTION" : "groups",

    "FIGURE" : "figures",
    "IMG" : "figures",
    "CANVAS" : "figures",
    "SVG" : "figures",

    "UL" : "list",
    "OL" : "list",

    "BUTTON" : "button",
    "A" : "link",
    "INPUT" : "input",

    "TABLE" : "table",
    "TD" : "table",
    "TFOOT" : "table",
    "TH" : "table",
    "TR" : "table",

    "AUDIO" : "multimedia",

    "FIELDSET" : "form",
    "FORM" : "form",
    "LABEL" : "form",
    "OPTION" : "form",
    "PROGRESS" : "form",
    "SELECT" : "form",
    "TEXTAREA" : "form",
}

