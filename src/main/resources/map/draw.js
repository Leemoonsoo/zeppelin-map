
    var map = new Datamap({
        element: document.getElementById(elementId),
        geographyConfig: {
            popupOnHover: false,
            highlightOnHover: false
        },
        fills: {
            defaultFill: '#ABDDA4',
            epic: 'red'
        },
        bubblesConfig: {
            borderWidth: 0,
            fillOpacity: 0.75
        }
    });

    map.bubbles(bubbledata, {
        popupTemplate: function(geo, data) {
          return '<div class="hoverinfo"></div>';
        }
    });
