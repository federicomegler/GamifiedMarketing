// Set new default font family and font color to mimic Bootstrap's default styling
Chart.defaults.global.defaultFontFamily = 'Nunito', '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
Chart.defaults.global.defaultFontColor = '#858796';

var n_people;
var dates;
var res;
$.get("GetStats", function(response){
	dates = Object.keys(response);
	n_people = Object.values(response);
	var ctx = document.getElementById("logChart");
	res = response;
var myLineChart = new Chart(ctx, {
  type: 'line',
  data: {
    labels: [dates[0].substring(4,11) + dates[0].substring(25,29),
				dates[1].substring(4,11) + dates[1].substring(25,29),
				dates[2].substring(4,11) + dates[2].substring(25,29),
				dates[3].substring(4,11) + dates[3].substring(25,29),
				dates[4].substring(4,11) + dates[4].substring(25,29),
				dates[5].substring(4,11) + dates[5].substring(25,29),
				dates[6].substring(4,11) + dates[6].substring(25,29)],
    datasets: [{
      label: "N. people",
      lineTension: 0.3,
      backgroundColor: "rgba(78, 115, 223, 0.05)",
      borderColor: "rgba(78, 115, 223, 1)",
      pointRadius: 3,
      pointBackgroundColor: "rgba(78, 115, 223, 1)",
      pointBorderColor: "rgba(78, 115, 223, 1)",
      pointHoverRadius: 3,
      pointHoverBackgroundColor: "rgba(78, 115, 223, 1)",
      pointHoverBorderColor: "rgba(78, 115, 223, 1)",
      pointHitRadius: 10,
      pointBorderWidth: 2,
      data: n_people,
    }],
  },
  options: {
    maintainAspectRatio: false,
    layout: {
      padding: {
        left: 10,
        right: 25,
        top: 25,
        bottom: 0
      }
    },
    scales: {
      xAxes: [{
        time: {
          unit: 'date'
        },
        gridLines: {
          display: false,
          drawBorder: false
        },
        ticks: {
          maxTicksLimit: 12
        }
      }],
      yAxes: [{
        ticks: {
          maxTicksLimit: 5,
          padding: 10,
          // Include a dollar sign in the ticks
          callback: function(value, index, values) {
            return '' + number_format(value);
          }
        },
        gridLines: {
          color: "rgb(234, 236, 244)",
          zeroLineColor: "rgb(234, 236, 244)",
          drawBorder: false,
          borderDash: [2],
          zeroLineBorderDash: [2]
        }
      }],
    },
    legend: {
      display: false
    },
    tooltips: {
      backgroundColor: "rgb(255,255,255)",
      bodyFontColor: "#858796",
      titleMarginBottom: 10,
      titleFontColor: '#6e707e',
      titleFontSize: 14,
      borderColor: '#dddfeb',
      borderWidth: 1,
      xPadding: 15,
      yPadding: 15,
      displayColors: false,
      intersect: false,
      mode: 'index',
      caretPadding: 10,
      callbacks: {
        label: function(tooltipItem, chart) {
          var datasetLabel = chart.datasets[tooltipItem.datasetIndex].label || '';
          return datasetLabel + ': ' + number_format(tooltipItem.yLabel);
        }
      }
    }
  }
});
});



function number_format(number, decimals, dec_point, thousands_sep) {
  // *     example: number_format(1234.56, 2, ',', ' ');
  // *     return: '1 234,56'
  number = (number + '').replace(',', '').replace(' ', '');
  var n = !isFinite(+number) ? 0 : +number,
    prec = !isFinite(+decimals) ? 0 : Math.abs(decimals),
    sep = (typeof thousands_sep === 'undefined') ? ',' : thousands_sep,
    dec = (typeof dec_point === 'undefined') ? '.' : dec_point,
    s = '',
    toFixedFix = function(n, prec) {
      var k = Math.pow(10, prec);
      return '' + Math.round(n * k) / k;
    };
  // Fix for IE parseFloat(0.55).toFixed(0) = 0;
  s = (prec ? toFixedFix(n, prec) : '' + Math.round(n)).split('.');
  if (s[0].length > 3) {
    s[0] = s[0].replace(/\B(?=(?:\d{3})+(?!\d))/g, sep);
  }
  if ((s[1] || '').length < prec) {
    s[1] = s[1] || '';
    s[1] += new Array(prec - s[1].length + 1).join('0');
  }
  return s.join(dec);
}




//pie chart

// Pie Chart Example

$.get("GetGenderStats", function (response){
	var genders = Object.keys(response);
	var count = Object.values(response);
	
	var pie = document.getElementById("GenderChart");
	var myPieChart = new Chart(pie, {
	  type: 'doughnut',
	  data: {
	    labels: genders,
	    datasets: [{
	      data: count,
	      backgroundColor: ['#4e73df', '#ffb3ff', '#eeeeee'],
	      hoverBackgroundColor: ['#2e59d9', '#ff99ff', '#dddddd'],
	      hoverBorderColor: "rgba(234, 236, 244, 1)",
	    }],
	  },
	  options: {
	    maintainAspectRatio: false,
	    tooltips: {
	      backgroundColor: "rgb(255,255,255)",
	      bodyFontColor: "#858796",
	      borderColor: '#dddfeb',
	      borderWidth: 1,
	      xPadding: 15,
	      yPadding: 15,
	      displayColors: false,
	      caretPadding: 10,
	    },
	    legend: {
	      display: false
	    },
	    cutoutPercentage: 80,
	  },
	});
	
	
});







// Bar Chart Example
var list
var days

$.get("GetSubmitStats", function (response){
	days = Object.keys(response);
	list = Object.values(response);
	
	
	var ctx = document.getElementById("SubmitChart");
	var myBarChart = new Chart(ctx, {
	  type: 'bar',
	  data: {
	    labels: [days[0].substring(4,11) + days[0].substring(25,29),
				days[1].substring(4,11) + days[1].substring(25,29),
				days[2].substring(4,11) + days[2].substring(25,29),
				days[3].substring(4,11) + days[3].substring(25,29),
				days[4].substring(4,11) + days[4].substring(25,29),
				days[5].substring(4,11) + days[5].substring(25,29),
				days[6].substring(4,11) + days[6].substring(25,29)],
	    datasets: [{
	      label: "Submitted",
	      backgroundColor: "#1cc88a",
	      hoverBackgroundColor: "#13865c",
	      borderColor: "#4e73df",
	      data: [list[0][0], list[1][0], list[2][0], list[3][0], list[4][0], list[5][0], list[6][0]],
	    },{
	      label: "Deleted",
	      backgroundColor: "#e74a3b",
	      hoverBackgroundColor: "#cc2819",
	      borderColor: "#4e73df",
	      data: [list[0][1], list[1][1], list[2][1], list[3][1], list[4][1], list[5][1], list[6][1]],
	    }],
	  },
	  options: {
	    maintainAspectRatio: false,
	    layout: {
	      padding: {
	        left: 10,
	        right: 25,
	        top: 25,
	        bottom: 0
	      }
	    },
	    scales: {
	      xAxes: [{
	        time: {
	          unit: 'date'
	        },
	        gridLines: {
	          display: false,
	          drawBorder: false
	        },
	        ticks: {
	          maxTicksLimit: 6
	        },
	        maxBarThickness: 25,
	      }],
	      yAxes: [{
	        ticks: {
	          maxTicksLimit: 5,
	          padding: 10,
	          // Include a dollar sign in the ticks
	          callback: function(value, index, values) {
	            return number_format(value);
	          }
	        },
	        gridLines: {
	          color: "rgb(234, 236, 244)",
	          zeroLineColor: "rgb(234, 236, 244)",
	          drawBorder: false,
	          borderDash: [2],
	          zeroLineBorderDash: [2]
	        }
	      }],
	    },
	    legend: {
	      display: true
	    },
	    tooltips: {
	      titleMarginBottom: 10,
	      titleFontColor: '#6e707e',
	      titleFontSize: 14,
	      backgroundColor: "rgb(255,255,255)",
	      bodyFontColor: "#858796",
	      borderColor: '#dddfeb',
	      borderWidth: 1,
	      xPadding: 15,
	      yPadding: 15,
	      displayColors: false,
	      caretPadding: 10,
	      callbacks: {
	        label: function(tooltipItem, chart) {
	          var datasetLabel = chart.datasets[tooltipItem.datasetIndex].label || '';
	          return datasetLabel + ": " + number_format(tooltipItem.yLabel);
	        }
	      }
	    },
	  }
	});

	
});


