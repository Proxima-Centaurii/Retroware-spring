//Get user prompts elements
var user_prompt = document.getElementById("username-prompt");
var password_prompt = document.getElementById("password-prompt");

var currentPrompt = -1;


//Get password strength elements
var password_strength = document.getElementById("password-strength");
var password_strength_value = document.getElementById("password-strength-value");

function showPrompt(x){
	if(currentPrompt == x)
		return;
	
	currentPrompt = x;
	
	switch(x){
		case 0:
			user_prompt.style.display = "block";
			password_prompt.style.display = "none";
			break;
		case 1:
			user_prompt.style.display = "none";
			password_prompt.style.display = "block";
			break;
		default:
			user_prompt.style.display = "none";
			password_prompt.style.display = "none";
			break;
	}
	
}

function calculatePasswordStrength(password){
    
    if(password.length == 0){
        password_strength.style.display = "none";
        return;
    }
    
    let E = entropy(password);
    
    if(E >= 75){
        password_strength_value.style.color = "limegreen";
        password_strength_value.innerHTML = "strong";
    }
    else if(E >= 50){
        password_strength_value.style.color = "gold";
        password_strength_value.innerHTML = "good";
    }
    else if(E >= 25){
        password_strength_value.style.color = "orange";
        password_strength_value.innerHTML = "weak";
    }
    else{
        password_strength_value.style.color = "orangered";
        password_strength_value.innerHTML = "very weak";
    }
    
    password_strength.style.display = "block";
}

// !|\¬`"£$%^&*()_-+=/?><.,:;@'~#[]{} 34 symols
function entropy(password){
  let uppercase_regex = /[A-Z]{1}/;
  let lowercase_regex = /[a-z]{1}/;
  let digit_regex = /[0-9]{1}/;
  let symbols_regex = /[!\|\\¬`\"£\$%\^&\*\(\)_\-\+=\/\?><\.,:;@'~#\[\]{}]{1}/;
  
  let L = password.length;
  let R = 0;
  
  if(uppercase_regex.test(password))
    R += 26;
  
  if(lowercase_regex.test(password))
    R += 26;
  
  if(digit_regex.test(password))
    R += 10;
  
  if(symbols_regex.test(password))
    R += 34;
  
  return R != 0 ? L * Math.log2(R) : 0;
}

