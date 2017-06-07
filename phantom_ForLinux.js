system=require('system');
var page = require('webpage').create();
var address=system.args[1];
var targetLanguage=system.args[2];
/*page.onConsoleMessage = function(msg) {
  console.log("page:"+msg);
}*/

page.open(address, function (status) {
	 if (status !== "success") {
		 console.log(status);
        console.log("Unable to access network");
		phantom.exit();
    } else {
        waitFor(function() {
            return page.evaluate(function() {
				console.log("checking Validation");
				return document.getElementsByClassName("ctvParagraph")[0].getAttribute("lang");
            });
        }, function() {
				return page.evaluate(function(){
					var targetElements=document.getElementsByClassName("ctvParagraph");
					var elementsNum=targetElements.length;
					var list=[];
					for(var i=0;i<elementsNum;i++){
						list[i]=targetElements[i].innerHTML;
					}
					return list;
				});
			});
		}
});

function waitFor(checkValidation,onReady){
	var condition;
	var retryTimes=0;
	interval=setInterval(function(){
		retryTimes++;
		if(condition!=targetLanguage && retryTimes<30){
			condition=checkValidation();
		}else if(retryTimes>=30){
			console.log("waitFor() time out.");
			phantom.exit();
		}
		else {
			var targetList=onReady();
			printTargetList(targetList);
			clearInterval(interval);
			phantom.exit();
		}
	},300);
}

function printTargetList(targetList){
	for(var i=0;i<targetList.length;i++){
		console.log(targetList[i])
	}
}


