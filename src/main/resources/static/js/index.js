
window.addEventListener("load", (event) => {
    const percentageDataBar = document.getElementsByClassName('percentage-data-bar-js');

    const move = async () => {
        for (let i = 0; i < percentageDataBar.length; i++) {
            await sleep(50);
            percentageDataBar[i].style.width = `${percentageDataBar[i].getAttribute('data-value')}%`;
          }
      
    }
    move();

    function sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    checkedOnClick = (value) => {
        let checkboxList = document.getElementsByClassName('checkbox-js');
        for(let i = 0; i < checkboxList.length; i++) {
            checkboxList[i].checked = false;
        }
        value.checked = true;
        console.log(value);
    }
});




