var colorSet = ['#145694', '#9EB9E4', '#FF6A00', '#FAAA5E', '#22931A'];
var limit = 0;
// Element will be initialized to insert element into page;
function Element() {
    this.li = $('<li></li>');
    this.label = $('<label for=\'\'></label>');
    this.checkbox = $('<input type= \'checkbox\'></label>');

}

var mapLoader = {
    onTokenAccessReady: function() {

        this.myMap = L.mapbox.map('map', 'liaokaien.lm91lne2', {
            maxZoom: 19,
            minZoom: 10
        });
        this.myMap.setView([40.443, -79.988], 13);
        return this;
    },
    addNode: function() {
        var $this = this; // =>mapLoader
        $.ajax({
            url: 'http://localhost:8080/TopicHood/GetTopicTweets',
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                    var tags = [];
                    var pieChartData = [];
                    for (var q = 0; q < 5; q++) {
                        //console.log(q, ': ', data.data[q].tweets.length);
                        tags.push(data.tags[q].name);
                        pieChartData.push([data.tags[q].name, data.tags[q].proportion]);
                        if (q === 0) {
                            limit = data.data[q].tweets.length;
                            //console.log(limit);
                        } else {
                            limit = (data.data[q].tweets.length < limit ? data.data[q].tweets.length : limit);
                            //console.log(limit);
                        }
                    }
                    limit = 5;
                    $this.tags = tags; //Convert local variable to function's variable
                    $this.myMap.featureLayer.setGeoJSON(GeoJson(data.data));
                    var count = 0;

                    // Add class tags[k] to No.count <img>
                    for (var k = 0; k < tags.length; k++) {
                        if (k === 0) {
                            count = 0;
                        } else {
                            count += limit;
                        }

                        for (var x = 0; x < limit; x++) {
                            var mapDot = $('.leaflet-marker-pane').find('img')[x + count];

                            if (mapDot && !mapDot.classList.contains(tags[k])) {
                                //console.log(x, count, x + count);
                                mapDot.classList.add(tags[k]);

                            } else {
                                console.log('F', x, count, k + count);
                            }

                        }
                    }


                    console.log($('.leaflet-marker-pane').find('img').length);
                    $('.leaflet-marker-pane').find('img').click(function() {
                        var img = $(this); //Dot on map.
                        var tweetId = $('.marker-description').text();
                        //console.log(tweetId);
                        $('.leaflet-popup-content').html('');
                        twttr.widgets.createTweet(tweetId, $('.leaflet-popup-content')[0]);
                        //console.log($('.leaflet-popup-content').find('iframe').css('visibility'));
                        window.setTimeout(function() {
                            if ($('.leaflet-popup-content').find('iframe').css('visibility') == 'hidden') {
                                $('.leaflet-popup-content').html('<p class=\'delete-tweet\'>Tweet has been deleted by author</p>');
                                img.remove();

                            }
                        }, 350);

                    });
                    //Load dropdown menus
                    $('#topic').click(function() {
                        $('#dd-topic').toggle();
                        if ($('#dd-topic').find('li').length === 0) {
                            for (var i = 0; i < $this.tags.length; i++) {
                                //console.log('e');
                                var lis = new Element();
                                $('#dd-topic').append(lis.li);
                                $('#dd-topic>li').eq(i).append(lis.checkbox.attr('id', $this.tags[i]));
                                $('#' + $this.tags[i]).attr('checked', true);
                                $('#dd-topic>li').eq(i).append(lis.label.attr('for', $this.tags[i]));
                                $('#dd-topic>li').eq(i).find('label').text($this.tags[i]);
                                $('#dd-topic>li').eq(i).find('input').click(onTopicClick);
                            }
                        }
                    });




                    //DRAW PIE CHART AND LINE CHART
                    var linePainter = new DataPainter();
                    linePainter.paintLineChart();
                    linePainter.paintPieChart(pieChartData);


                } // end recall
        }); //end ajax
        return this;
    },
    loadTopic: function() {
        $this = this;
    }
};



function GeoJson(data) {
    nodes = [];
    // i = topic number
    for (var i = 0; i < data.length; i++) {
        // t= the num of each tweet within one topic

        for (var t = 0; t < limit; t++) {
            var node = {};
            node.type = 'Feature';
            node.geometry = {
                type: 'Point',
                coordinates: [data[i].tweets[t].geo[1],
                    data[i].tweets[t].geo[0]
                ]
            };
            node.properties = {
                'marker-color': colorSet[i],
                'marker-size': 'small',
                'description': data[i].tweets[t].id.toString()
            };
            nodes.push(node);
        }
        // We have add nodes of one topic to the map.
    }
    //console.log(limit);
    console.log(nodes.length);
    return nodes;
}

function DataGenerate() {}

DataGenerate.prototype = {
    getLineChartData: function() {
        var value = [{
            x: 1,
            y: 15
        }, {
            x: 2,
            y: 23
        }, {
            x: 3,
            y: 37
        }, {
            x: 4,
            y: 49
        }, {
            x: 5,
            y: 52
        }, {
            x: 6,
            y: 67
        }, {
            x: 7,
            y: 32
        }];
        return [{
            values: value,
            key: '#Pitt',
            color: '#2ca02c'
        }];

    },

    getPieChartData: function(data) {
        function Value(l, v) {
            this.label = l;
            this.value = v;
        }
        var result = [];
        for (var i = 0; i < data.length; i++) {
            result.push(new Value(data[i][0], data[i][1]));
        }
        return result;
    }
};


function DataPainter() {

}
DataPainter.prototype = {
    paintLineChart: function(data) {
        nv.addGraph(function() {
            //console.log('Painting the line chart');
            var chart = nv.models.lineChart()
                .margin({
                    left: 100
                }) //Adjust chart margins to give the x-axis some breathing room.
                .useInteractiveGuideline(true) //We want nice looking tooltips and a guideline!
                .showLegend(true) //Show the legend, allowing users to turn on/off line series.
                .showYAxis(true) //Show the y-axis
                .showXAxis(true) //Show the x-axis
            ;
            chart.xAxis //Chart x-axis settings
                .axisLabel('Date');

            chart.yAxis //Chart y-axis settings
                .axisLabel('Frequency')
                .tickFormat(d3.format('d'));

            var ge = new DataGenerate();
            var lineChartData = ge.getLineChartData();
            d3.select('#chart-line svg') //Select the <svg> element you want to render the chart in.
                .datum(lineChartData) //Populate the <svg> element with chart data...
                .call(chart);
            nv.utils.windowResize(function() {
                chart.update();
            });
            return chart;
        });
    },
    paintPieChart: function(data) {

        nv.addGraph(function() {
            var chart = nv.models.pieChart()
                .x(function(d) {
                    return d.label;
                })
                .y(function(d) {
                    return d.value;
                })
                .showLabels(true) //Display pie labels
                .labelThreshold(0.05) //Configure the minimum slice size for labels to show up
                .labelType("percent") //Configure what type of data to show in the label. Can be "key", "value" or "percent"
                .donut(true) //Turn on Donut mode. Makes pie chart look tasty!
                .donutRatio(0.35) //Configure how big you want the donut hole size to be.
            ;
            var ge = new DataGenerate();
            d3.select("#chart-pie svg")
                .datum(ge.getPieChartData(data))
                .transition().duration(350)
                .call(chart);

            return chart;
        });
    }
};


$(document).ready(function() {
    // Load Map
    // use mapLoader.addNode() to refresh the view;
    L.mapbox.accessToken = 'pk.eyJ1IjoibGlhb2thaWVuIiwiYSI6IkNVSndxVlUifQ.7LsEhdgYXzlK4MH_U_6c0w';
    mapLoader.onTokenAccessReady().addNode().loadTopic();
    //Load Carousel
    $('#chart').owlCarousel({
        //navigation : true, // Show next and prev buttons
        slideSpeed: 3000,
        paginationSpeed: 400,
        singleItem: true
    });

    //Get data and display the line chart
});


function onTopicClick() {
    var isChecked = $(this).prop('checked');
    console.log(typeof isChecked);
    var c = 0;
    if ($(this).prop('checked') === false) {
        $('.' + $(this).attr('id')).each(function() {
            $(this).hide();

        });
    } else {
        $('.' + $(this).attr('id')).each(function() {
            $(this).show();
        });

    }

}
