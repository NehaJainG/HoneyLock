// Define the function
export function generateLargeRandomInteger(digits = 20) {
    let result = "";
    
    // Loop to generate each digit or small group of digits
    while (result.length < digits) {
      const randomChunk = Math.floor(Math.random() * 1e6).toString(); // generates up to 6 random digits
      result += randomChunk;
    }
    
    // Trim the result to the exact number of digits specified
    result = result.slice(0, digits);
    
    return result;
  }
  