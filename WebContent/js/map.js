var colorSet = ['#145694', '#9EB9E4', '#FF6A00', '#FAAA5E', '#22931A'];
var isOverflow = false;
var isInit = false;
// Element will be initialized to insert element into page;
function Element() {
    this.li = $('<li></li>');
    this.label = $('<label for=\'\'></label>');
    this.checkbox = $('<input type= \'checkbox\'></label>');

}

var mapLoader = {
    onTokenAccessReady: function() {
        var southWest = L.latLng(39.329, -81.130),
            northEast = L.latLng(41.519, -78.825),
            bounds = L.latLngBounds(southWest, northEast);
        this.myMap = L.mapbox.map('map', 'liaokaien.lm91lne2', {
            maxBounds: bounds,
            maxZoom: 19,
            minZoom: 10
        });
        this.myMap.setView([40.443, -79.988], 13);
        return this;
    },
    addNode: function() {
        var $this = this; // =>mapLoader
        var topics = $('.topiclist').val() + '';
        var neighbors = $('.neighborlist').val() + '';
        neighbors = neighbors.split(',');
        var neighborhood = '';
        for (var i = 0; i < neighbors.length; i++) {
            neighbors[i] = "'" + neighbors[i] + "',";
            neighborhood += neighbors[i];
        }
        neighborhood = neighborhood.slice(0, neighborhood.length - 1);
        console.log(neighborhood);
        neighborhood = neighborhood.replace(/\+/g, ' ');
        console.log(neighborhood);
        var time = '';
        if (!time) {
            time = 'day';
        }

        $.ajax({
            url: 'GetTopicTweets',
            type: 'POST',
            data: {
                'topics': topics,
                'neighborhood': neighborhood
            },
            success: function(data) {
                    var tags = [];
                    var pieChartData = [];
                    var lineChartData = [];
                    var nodes = [];
                    var edges = [];
                    for (var q = 0; q < data.tags.length; q++) {
                        tags.push(data.tags[q].name);
                        pieChartData.push([data.tags[q].name, data.tags[q].proportion]);
                        lineChartData.push(data.tags[q].points);
                    }
                    var i;
                    // data.related.nodes
                    for (i = 0; i < 10; i++) {
                        nodes.push({
                            id: i,
                            label: data.related.nodes[i]
                        });
                    }

                    for (i = 0; i < 25; i++) {
                        edges.push({
                            from: data.related.relations[i].from,
                            to: data.related.relations[i].to,
                            value: data.related.relations[i].value
                        });
                    }
                    console.log(nodes, edges);
                    //Convert local variable to function's variable
                    $this.tags = tags;
                    //Refresh dots
                    console.log('success');
                    $this.myMap.featureLayer.setGeoJSON(GeoJson(data.data));
                    var count = 0;
                    // Add class tags[k] to No.count <img>
                    for (var k = 0; k < tags.length; k++) {
                        if (k === 0) {
                            count = 0;
                        } else {
                            count += data.data[k - 1].tweets.length;
                        }
                        for (var x = 0; x < data.data[k].tweets.length; x++) {
                            var mapDot = $('.leaflet-marker-pane').find('img')[x + count];
                            if (mapDot && !mapDot.classList.contains(tags[k])) {
                                mapDot.classList.add(tags[k]);

                            } else {}

                        }
                    } // end for(){}
                    // addEventListenr to img (dots);
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
                        }, 1000);
                    });
                    //Load dropdown menus
                    //DRAW PIE CHART AND LINE CHART
                    var linePainter = new DataPainter();
                    linePainter.paintLineChart(tags, lineChartData);
                    linePainter.paintPieChart(pieChartData);
                    linePainter.paintNetwork(nodes, edges);
                } // end recall
        }); //end ajax
        return this;
    },
    initMenu: function() {
        $this = this;
        $.ajax({
            url: '/TopicHood/GetTopicNeighborList',
            success: function(data) {
                var s = '';
                for (var i = 0; i < data.topicList.length; i++) {
                    if (i < 5) {
                        s += '<option value="' + data.topicList[i].id + '" selected>';
                        s += data.topicList[i].name + '</option>';
                    } else {
                        s += '<option value="' + data.topicList[i].id + '">';
                        s += data.topicList[i].name + '</option>';
                    }
                }
                var t = '';
                for (i = 0; i < data.neighborList.length; i++) {
                    if (i < 5) {
                        t += '<option value="' + data.neighborList[i] + '" selected>';
                        t += data.neighborList[i] + '</option>';
                    } else {
                        t += '<option value="' + data.neighborList[i] + '">';
                        t += data.neighborList[i] + '</option>';
                    }
                }
                //Load topic list
                $('.topiclist').append(s);
                $('.topiclist').attr('data-live-search', 'true');
                $('.topiclist').selectpicker('selectAll');
                //console.log($('.topicList'));
                $(document).on('change', '.topiclist', function() {
                    $this.addNode();
                });
                //load neighborlist
                $('.neighborlist').append(t);
                $('.neighborlist').attr('data-live-search', 'true');
                $('.neighborlist').selectpicker();
                $(document).on('change', '.neighborlist', function() {
                    $this.addNode();
                });
                $('.timelist').selectpicker();
                $(document).on('change', '.timelist', function() {
                    $this.addNode();
                });
                $this.addNode();
            }
        });
    }
};

function GeoJson(data) {
    nodes = [];
    // i = topic number
    for (var i = 0; i < data.length; i++) {
        // t= the num of each tweet within one topic

        for (var t = 0; t < data[i].tweets.length; t++) {
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
    return nodes;
}

function DataGenerate() {}

DataGenerate.prototype = {
    getLineChartData: function(tags, points) {
        //data = data.tags.
        var result = [];
        for (var i = 0; i < tags.length; i++) {
            result.push({
                values: points[i],
                key: tags[i],
                color: colorSet[i]
            });
        }
        return result;
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
    },
    getNetworkData: function(nodes, edges) {
        var data = {
            nodes: nodes,
            edges: edges,
        };
        return data;
    }
};


function DataPainter() {

}
DataPainter.prototype = {
    paintLineChart: function(tags, points) {
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
            var lineChartData = ge.getLineChartData(tags, points);
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
    },
    paintNetwork: function(nodes, edges) {
        var container = document.getElementById('chart-network');
        var options = {
            width: '360px',
            height: '380px'
        };
        var ge = new DataGenerate();
        var data = ge.getNetworkData(nodes, edges);
        var network = new vis.Network(container, data, options);

    }
};


$(document).ready(function() {
    // Load Map
    // use mapLoader.addNode() to refresh the view;
    L.mapbox.accessToken = 'pk.eyJ1IjoibGlhb2thaWVuIiwiYSI6IkNVSndxVlUifQ.7LsEhdgYXzlK4MH_U_6c0w';
    mapLoader.onTokenAccessReady().initMenu();


    //Load Carousel
    $('#chart').owlCarousel({
        //navigation : true, // Show next and prev buttons
        slideSpeed: 3000,
        paginationSpeed: 400,
        singleItem: true
    }); //Get data and display the line chart
    console.log('reload');


});


function onTopicClick() {
    var isChecked = $(this).prop('checked');
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

function quo(str) {
    str = '\'' + str + '\'';
    return str;
}
