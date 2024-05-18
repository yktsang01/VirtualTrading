function dropMember() {
  document.getElementById("memberDropdown").classList.toggle("show");
}

window.onclick = function(m) {
  if (!m.target.matches('.dropbtn')) {
    var memberDropdown = document.getElementById("memberDropdown");
    if (memberDropdown.classList.contains('show')) {
      memberDropdown.classList.remove('show');
    }
  }
}

function includeHTML() {
  var z, i, elmnt, file, xhttp;
  z = document.getElementsByTagName("*");
  for (i = 0; i < z.length; i++) {
    elmnt = z[i];
    file = elmnt.getAttribute("w3-include-html");
    if (file) {
      xhttp = new XMLHttpRequest();
      xhttp.onreadystatechange = function() {
        if (this.readyState == 4) {
          if (this.status == 200) {
            elmnt.innerHTML = this.responseText;
          }
          if (this.status == 404) {
            elmnt.innerHTML = "Page not found.";
          }
          elmnt.removeAttribute("w3-include-html");
          includeHTML();
        }
      }
      xhttp.open("GET", file, true);
      xhttp.send();
      return;
    }
  }
}

function refreshDashboard() {
  var currencies = document.getElementById("dashboardCurrency");
  var ccyCode = currencies.options[currencies.selectedIndex].value;
  if(ccyCode == "#") {
    window.location.href = "/member/dashboard";
  } else {
    window.location.href = "/member/dashboard?ccy="+ccyCode;
  }
}

function refreshIndex() {
  var currencies = document.getElementById("indexCurrency");
  var ccyCode = currencies.options[currencies.selectedIndex].value;
  if(ccyCode == "#") {
    window.location.href = "/member/indices";
  } else {
    window.location.href = "/member/indices?ccy="+ccyCode;
  }
}

function refreshEquity() {
  var currencies = document.getElementById("equityCurrency");
  var ccyCode = currencies.options[currencies.selectedIndex].value;
  if(ccyCode == "#") {
    window.location.href = "/member/equities";
  } else {
    window.location.href = "/member/equities?ccy="+ccyCode;
  }
}

function refreshAccount() {
  var currencies = document.getElementById("accountCurrency");
  var ccyCode = currencies.options[currencies.selectedIndex].value;
  if(ccyCode == "#") {
    window.location.href = "/member/accountBalance";
  } else {
    window.location.href = "/member/accountBalance?ccy="+ccyCode;
  }
}

function refreshAccountTxn() {
  var currencies = document.getElementById("accountTxnCurrency");
  var ccyCode = currencies.options[currencies.selectedIndex].value;
  if(ccyCode == "#") {
    window.location.href = "/member/accountTxn";
  } else {
    window.location.href = "/member/accountTxn?ccy="+ccyCode;
  }
}

function refreshBank() {
  var currencies = document.getElementById("bankCurrency");
  var ccyCode = currencies.options[currencies.selectedIndex].value;
  if(ccyCode == "#") {
    window.location.href = "/member/bankInfo";
  } else {
    window.location.href = "/member/bankInfo?ccy="+ccyCode;
  }
}

function refreshBankTxn() {
  var currencies = document.getElementById("bankTxnCurrency");
  var ccyCode = currencies.options[currencies.selectedIndex].value;
  if(ccyCode == "#") {
    window.location.href = "/member/bankTxn";
  } else {
    window.location.href = "/member/bankTxn?ccy="+ccyCode;
  }
}

function refreshTradingTxn() {
  var currencies = document.getElementById("tradingTxnCurrency");
  var ccyCode = currencies.options[currencies.selectedIndex].value;
  if(ccyCode == "#") {
    window.location.href = "/member/tradingTxn";
  } else {
    window.location.href = "/member/tradingTxn?ccy="+ccyCode;
  }
}

function refreshOutstandingTxn() {
  var currencies = document.getElementById("osTxnCurrency");
  var ccyCode = currencies.options[currencies.selectedIndex].value;
  if(ccyCode == "#") {
    window.location.href = "/member/outstandingTxn";
  } else {
    window.location.href = "/member/outstandingTxn?ccy="+ccyCode;
  }
}

function refreshPortfolio() {
  var currencies = document.getElementById("portfolioCurrency");
  var ccyCode = currencies.options[currencies.selectedIndex].value;
  if(ccyCode == "#") {
    window.location.href = "/member/portfolio";
  } else {
    window.location.href = "/member/portfolio?ccy="+ccyCode;
  }
}

function refreshTransfer() {
  var currencies = document.getElementById("accountCcy");
  var ccyCode = currencies.options[currencies.selectedIndex].value;
  if(ccyCode == "#") {
    window.location.href = "/member/transferFunds";
  } else {
    window.location.href = "/member/transferFunds?ccy="+ccyCode;
  }
}

/* specific to /member/tradingTxn.html */
function checkNewOption() {
    var portfolios = document.getElementById("portfolio");
    var newOption = portfolios.options[portfolios.selectedIndex].value;
    if(newOption == "New") {
        document.getElementById("portName").disabled = false;
        document.getElementById("portCcy").disabled = false;
    } else {
        document.getElementById("portName").disabled = true;
        document.getElementById("portCcy").disabled = true;
    }
}
