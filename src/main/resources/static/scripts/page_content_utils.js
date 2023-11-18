/**
 * This function is called from outside this script in order to update content loaded
 * through fetch calls.
 */
function fetchedNewContent(){
    displayRatings();
    displayCounts();
}

// === RATINGS ===
//Update game ratings text based on value
function displayRatings(){
        var list = document.getElementsByClassName('game-rating-label');
        var length = list.length;

        console.log("Found: "+length + " elements.");

        for(let i = 0; i < length; i++){
            let value = list[0].getAttribute('value');

            //Very positive, positive, mixed, negative, very negative

            if(value >= 80)
                list[0].classList.add('very-positive');
            else if(value >= 60)
                list[0].classList.add('positive');
            else if (value >= 40)
                list[0].classList.add('mixed');
            else if (value >= 20)
                list[0].classList.add('negative');
            else if (value > 0)
                list[0].classList.add('very-negative')
            else
                list[0].classList.add('no-ratings');

            list[0].classList.toggle('game-rating-label');
        }
    }

displayRatings();

// === COUNTS ===
/* Formats integer values for rating counts (likes/dislikes) and play couns
* The resulted text is a short text that displays the most significant digits
* and the 'order of magnitude' of the number.
*/
function displayCounts(){
    let orders = ['','K','M','B','T'];
    
    let list = document.getElementsByClassName('user-count');
    let length = list.length;
    
    console.log("Found: "+length + " elements.");
    
    for(let i = 0; i < length; i++){
        // Ensure that the value read is a number. Set it to 0 if it's not.
        let value = parseInt(list[0].getAttribute('value'));
        value = isNaN(value) ? 0 : value;

        // Is the number in the thousands, millions, billions etc ?
        let power = (value == 0) ? 0 : parseInt(Math.log10(value)/3);
        value = value/Math.pow(1000,power);
        
        // Sometimes the '.' is the last character eg: 10.001 -> 10K instead of 10.K
        let display_text = value.toString().substring(0,3);
        if(display_text.endsWith('.'))
            display_text = display_text.substring(0,2) + orders[power];
        else
            display_text = display_text + orders[power];

        // Update the html component
        list[0].textContent = display_text;
        list[0].classList.toggle('user-count');
    }
}

displayCounts();


// === GAME TITLE OVERFLOW / TITLE SLIDER === 
/* Slide game title if its title exceeds list element's div size
    Requires to be placed on each element using the "onpointer + (leave/enter)" function
    passing "this" as an argument
    NOTE: requires animations to be loaded from CSS file
 */    
function startTitleSlide(button){
    let title = button.getElementsByClassName("game-title")[0];
    
    if(title.scrollWidth > button.clientWidth){
        title.style.overflow = "unset";
        title.style.animation = "title-slide-animation 10s linear infinite";
    }
}

function stopTitleSlide(button){
    let title = button.getElementsByClassName("game-title")[0];
    
    title.style.overflow = "clip";
    title.style.animation = "";
}

// === MODALS ===

/*  Get all the modals present in page by class (class = 'modal').
 * The resulting object is converted from "HTMLCollection" to
 * an array so it can be iterated over with forEach
 */
var modals = (function() {
    let arr = [];
    let htmlCollection = document.getElementsByClassName('modal');
    let len = htmlCollection.length;

    for(i=0;i<len;i++){
        arr.push(htmlCollection[i]);
    }
  
    return arr;
})();

/* Checking if a modal background has been clicked to close the respective modal.
This has to be done via the "window.onclick" event to correctly differentiate between
the modal background and the modal content on click.
*/
window.onclick = function(event){
    modals.forEach((item) => {
        if(event.target == item){
            console.log('Found one!')
            item.style.display = "none";
        }
    });
}
        
function closeModal(modal_id){
    document.getElementById(modal_id).style.display = 'none';
}
        
function showModal(modal_id){
    document.getElementById(modal_id).style.display = 'block';
}
        
