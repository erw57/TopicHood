var colorSet = ['#145694', '#9EB9E4', '#FF6A00', '#FAAA5E', '#22931A'];
colorSet.push('#82D571', '#C4111B', '#F47E7F', '#7D4BAC', '#B198C4');

function findIndex(obj, str) {
    // Find the index of items in network data set
    for (var i = 0; i < obj.length; i++) {
        if (str === obj[i].label)
            return obj[i].id;
    }
    return -1;
}

function showLoading() {
    var height = $(document).height();
    var width = $(document).width();
    $('.mask').css('height', height);
    $('.mask').css('width', width);
    $('.mask').show();
}

function hideLoading() {
    $('.mask').hide();
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
        neighborhood = neighborhood.replace(/\+/g, ' ');
        var time = $('.timelist').val() + '';
        if (!time) {
            //default time-span value: 'day'
            time = 'day';
        }
        if(topics == 'null' || neighborhood == "'null'"){
        	alert("please select at least one topic and one neighborhood");
        }
        else{
        	showLoading();
            $.ajax({
                url: 'GetTopicTweets',
                type: 'POST',
                data: {
                    'topics': topics,
                    'neighborhood': neighborhood,
                    'time': time
                },
                success: function(data) {
                        var tags = [],
                            pieChartData = [],
                            lineChartData = [],
                            nodes = [],
                            edges = [],
                            tweetsCount = 0;
                        // prepare data set for the line chart & the pie chart
                        for (var q = 0; q < data.tags.length; q++) {
                            tags.push(data.tags[q].name);
                            tweetsCount += data.tags[q].volume;
                            pieChartData.push([data.tags[q].name, data.tags[q].proportion]);
                            lineChartData.push(data.tags[q].points);
                        }
                        $('#tweets-count').text(tweetsCount);
                        var i;
                        // Construct the dataset for network graph,
                        // including nodes and edges
                        for (i = 0; i < data.related.nodes.length; i++) {
                            nodes.push({
                                id: i,
                                label: data.related.nodes[i],
                                color: {
                                    background: colorSet[i],
                                    border: colorSet[i],
                                },
                                fontColor: 'white'
                            });
                        }
                        for (i = 0; i < data.related.relations.length; i++) {
                            edges.push({
                                from: findIndex(nodes, data.related.relations[i].from),
                                to: findIndex(nodes, data.related.relations[i].to),
                                value: data.related.relations[i].value
                            });
                        }
                        //Convert local variable to function's variable
                        $this.tags = tags;
                        //Refresh dots
                        $this.myMap.featureLayer.setGeoJSON(GeoJson(data.data));
                        var count = 0;
                        // Add class tags[k] to No.count <img>. Not useful in
                        // new page loading mechanism
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
                                }
                            }
                        } // end for(){}
                        // addEventListenr to img (dots);
                        $('.leaflet-marker-pane').find('img').click(function() {
                            $('.leaflet-popup').show();
                            var img = $(this); //Dot on map.
                            var tweetId = $('.marker-description').text();
                            $('.leaflet-popup-content').html('');
                            // Add event listener to the close button in tweet popup window
                            // If clicked, hide the popup.
                            $('.leaflet-popup-content').append('<i class = "fa fa-times"></i>');
                            $('.leaflet-popup-content>i').click(function() {
                                $('.leaflet-popup').hide();
                            });
                            twttr.widgets.createTweet(tweetId, $('.leaflet-popup-content')[0]);
                            // if the content of tweet cannot be loaded within 1000 ms
                            // remove it and tell user that it has been deleted by its author
                            window.setTimeout(function() {
                                if ($('.leaflet-popup-content').find('iframe').css('visibility') == 'hidden') {
                                    $('.leaflet-popup-content').html('<p class=\'delete-tweet\'>Tweet has been deleted by author</p>');
                                    img.remove();
                                }
                            }, 1000);
                        });
                        //DRAW PIE CHART AND LINE CHART
                        var linePainter = new DataPainter();
                        linePainter.paintLineChart(tags, lineChartData);
                        linePainter.paintPieChart(pieChartData);
                        linePainter.paintNetwork(nodes, edges);
                        hideLoading();
                    } // end recall
            }); //end ajax
        }
        
        return this;
    },
    initMenu: function() {
        $this = this;
        $.ajax({
            url: '/TopicHood/GetTopicNeighborList',
            success: function(data) {
                // s ===> the content of topic menu
                var s = '';
                for (var i = 0; i < data.topicList.length; i++) {
                    if (i < 4) {
                        s += '<option value="' + data.topicList[i].id + '" selected>';
                        s += data.topicList[i].name + '</option>';
                    } else {
                        s += '<option value="' + data.topicList[i].id + '">';
                        s += data.topicList[i].name + '</option>';
                    }
                }
                // t ===> the content of neighborhood menu
                var t = '';
                for (i = 0; i < data.neighborList.length; i++) {
                    if (i < 3) {
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
                $('.topiclist').selectpicker({
                    style: 'selectBox',
                    size: 10
                });
                //When any selection in the control panel changes,
                //reload the map and graph area;
                $(document).on('change', '.topiclist', function() {
                    $this.addNode();
                });
                //Load neighborlist
                $('.neighborlist').append(t);
                $('.neighborlist').attr('data-live-search', 'true');
                $('.neighborlist').selectpicker({
                    style: 'selectBox',
                    size: 10
                });
                $(document).on('change', '.neighborlist', function() {
                    $this.addNode();
                });
                //Load time-span list
                $('.timelist').selectpicker({
                    style: 'selectBox',
                    size: 10
                });
                $(document).on('change', '.timelist', function() {
                    $this.addNode();
                });
                $this.addNode();
            }
        });
    }
};


function GeoJson(data) {
    //construct GeoJson object which is the parameter of this.addNode()
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
    // Load Map. Use mapLoader.addNode() to refresh the view;
    L.mapbox.accessToken = 'pk.eyJ1IjoibGlhb2thaWVuIiwiYSI6IkNVSndxVlUifQ.7LsEhdgYXzlK4MH_U_6c0w';
    //Initialize the map and menus
    mapLoader.onTokenAccessReady().initMenu();


    //Load Carousel
    $('#chart').owlCarousel({
        slideSpeed: 3000,
        paginationSpeed: 400,
        singleItem: true,
        mouseDrag: false
    });
    // Load popup menu
    $('.open-popup-link').magnificPopup({
        type: 'inline',
        // Allow opening popup on middle mouse click.
        //Always set it to true if you don't provide alternative source in href.
        midClick: true
    });

});
