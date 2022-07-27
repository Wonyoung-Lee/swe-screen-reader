import React, { useRef, useEffect } from 'react'


function Canvas (props) {

    const canvasWays = useRef(null)
    const canvasRef = useRef(null)

    let INIT_MAX_LAT = 41.828147
    let INIT_MIN_LAT = 41.823142
    let INIT_MAX_LON = -71.392231
    let INIT_MIN_LON = -71.407971

    let tileWidth = INIT_MAX_LON - INIT_MIN_LON // 0.01574 * 38119.44091 -> 600
    let tileHeight = INIT_MAX_LAT - INIT_MIN_LAT // 0.005005 * 139860.1399 -> 700

    const mapMinLat = 40.1581762
    const mapMaxLat = 42.0952906
    const mapMinLon = -73.7485663
    const mapMaxLon = -70.5590942
    let convertY = function(latitude, tileYCoord) {
        return (mapMinLat + tileHeight * (tileYCoord + 1) - latitude) * (400 / tileHeight)
    }
    let convertX = function(longitude, tileXCoord) {
        return (longitude - (mapMinLon + tileWidth * tileXCoord)) * (700 / tileWidth)
    }
    let concat = function(str1, str2) {return String(str1).concat(":", String(str2))}

    let cache = {}

    /**
     * returns ways
     * @returns {Promise<unknown>}
     */
    async function requestWays(tileXCoord, tileYCoord) {
        const maxLat = mapMinLat + tileHeight * (tileYCoord + 1)
        const minLat = mapMinLat + tileHeight * tileYCoord
        const maxLon = mapMinLon + tileWidth * (tileXCoord + 1)
        const minLon = mapMinLon + tileWidth * tileXCoord
        console.log(maxLat, minLat, maxLon, minLon)

        return new Promise( (resolve, reject) => {
                // Address we are getting data from
                fetch("http://localhost:4567/ways", {
                    method: 'POST',
                    headers: {
                        "Content-Type": "application/json",
                        'Access-Control-Allow-Origin': '*',
                    },
                    // Data that we are inputting into the api. This allows us to get the ways for a specific area
                    body: JSON.stringify([maxLat, minLat, maxLon, minLon])
                }).then(response => response.json())
                    .then(response => {
                        // console.log("Response:", response)
                        if('error' in response) {
                            if (response.error === undefined) {
                                alert("An error occurred")
                            } else {
                                alert(response.error)
                            }
                            reject()
                        } else {
                            cache[concat(tileXCoord, tileYCoord)] = response
                            // console.log("ways:", response[0])
                            resolve( {
                                "ways" : response
                            })
                        }
                    })
            }
        )
    }

    async function draw(tileXCoordinate, tileYCoordinate, ctx) {
        console.log(tileXCoordinate, tileYCoordinate)
        if (concat(tileXCoordinate, tileYCoordinate) in cache) { // If cached
            // console.log("cached array: " + cache[concat(tileXCoordinate, tileYCoordinate)])
            console.log("cached")
            canvasWays.current.ways = cache[concat(tileXCoordinate, tileYCoordinate)]
        } else {
            await requestWays(tileXCoordinate, tileYCoordinate).then(ways => canvasWays.current = ways)
        }

        let alreadyDrawn = [] //keeps track of street names we've already drawn

        // Stroke road outline
        ctx.beginPath();
        ctx.strokeStyle = 'rgba(103,103,103,0.78)'
        ctx.lineWidth = 3
        for (let i = 0; i < canvasWays.current.ways.length; i++) {

            // Instantiate coordinates
            let x1 = convertX(canvasWays.current.ways[i].startLon, tileXCoordinate) //x coordinate for start node
            let y1 = convertY(canvasWays.current.ways[i].startLat, tileYCoordinate) //y coordinate for start node
            let x2 = convertX(canvasWays.current.ways[i].endLon, tileXCoordinate) //x coordinate for end node
            let y2 = convertY(canvasWays.current.ways[i].endLat, tileYCoordinate) //y coordinate for end node
            // Instantiate name and type properties
            let name = canvasWays.current.ways[i].name
            let type = canvasWays.current.ways[i].type

            // Color handling
            ctx.save()
            if (type != null && type !== "") {
                ctx.moveTo(x1, y1)
                ctx.lineTo(x2, y2)
            }
        }
        ctx.stroke() // finishes path

        // Stroke road
        ctx.beginPath();
        ctx.strokeStyle = '#c5c5c5'
        ctx.lineWidth = 2
        for (let i = 0; i < canvasWays.current.ways.length; i++) {

            // Instantiate coordinates
            let x1 = convertX(canvasWays.current.ways[i].startLon, tileXCoordinate) //x coordinate for start node
            let y1 = convertY(canvasWays.current.ways[i].startLat, tileYCoordinate) //y coordinate for start node
            let x2 = convertX(canvasWays.current.ways[i].endLon, tileXCoordinate) //x coordinate for end node
            let y2 = convertY(canvasWays.current.ways[i].endLat, tileYCoordinate) //y coordinate for end node
            // Instantiate name and type properties
            let name = canvasWays.current.ways[i].name
            let type = canvasWays.current.ways[i].type

            // Color handling
            ctx.save()
            if (type != null && type !== "") {
                ctx.moveTo(x1, y1)
                ctx.lineTo(x2, y2)
            }
        }
        ctx.stroke() // finishes path

        // Stroke buildings
        ctx.beginPath();
        ctx.strokeStyle = '#c2e5c0'
        // ctx.globalAlpha = 0.5
        ctx.lineWidth = 2
        for (let i = 0; i < canvasWays.current.ways.length; i++) {

            // Instantiate coordinates
            let x1 = convertX(canvasWays.current.ways[i].startLon, tileXCoordinate) //x coordinate for start node
            let y1 = convertY(canvasWays.current.ways[i].startLat, tileYCoordinate) //y coordinate for start node
            let x2 = convertX(canvasWays.current.ways[i].endLon, tileXCoordinate) //x coordinate for end node
            let y2 = convertY(canvasWays.current.ways[i].endLat, tileYCoordinate) //y coordinate for end node

            // Instantiate name and type properties
            let name = canvasWays.current.ways[i].name
            let type = canvasWays.current.ways[i].type

            // Color handling
            if (type == null || type == "") { // buildings
                ctx.moveTo(x1, y1)
                ctx.lineTo(x2, y2)
            }
        }
        ctx.stroke() // finishes path

        // Stroke labels
        ctx.beginPath();
        ctx.fillStyle = 'black'
        ctx.globalAlpha = 1
        ctx.lineWidth = 3
        for (let i = 0; i < canvasWays.current.ways.length; i++) {

            // Instantiate coordinates
            let x1 = convertX(canvasWays.current.ways[i].startLon, tileXCoordinate) //x coordinate for start node
            let y1 = convertY(canvasWays.current.ways[i].startLat, tileYCoordinate) //y coordinate for start node
            let x2 = convertX(canvasWays.current.ways[i].endLon, tileXCoordinate) //x coordinate for end node
            let y2 = convertY(canvasWays.current.ways[i].endLat, tileYCoordinate) //y coordinate for end node

            // Instantiate name and type properties
            let name = canvasWays.current.ways[i].name
            let type = canvasWays.current.ways[i].type

            // Instantiate trigonometry for label orientation
            let angle = 0;
            let adj = x2 - x1
            let opp = y2 - y1
            if (x1 < x2 && y1 < y2 || x1 < x2 && y1 > y2) { // NE, SE
                angle = Math.asin(-(opp) / Math.sqrt(Math.pow(adj, 2) + Math.pow(opp, 2)))
            } else if (x1 > x2 && y1 < y2 || x1 > x2 && y1 > y2) { // NW
                angle = Math.asin((opp) / Math.sqrt(Math.pow(adj, 2) + Math.pow(opp, 2)))
            }

            // Color handling
            ctx.save()
            if (type != null && type !== "") {
                if (!alreadyDrawn.includes(name)) {
                    ctx.font = "10px Arial"
                    ctx.translate(x1, y1)
                    ctx.rotate(angle)
                    ctx.textAlign = "center"
                    ctx.fillText(name, 0, 0)
                    alreadyDrawn.push(name)
                    ctx.restore()
                }
            }
        }
        ctx.stroke() // finishes path
    }

    useEffect( () => {

        const canvas = canvasRef.current
        canvas.width = 700
        canvas.height = 400
        const ctx = canvas.getContext('2d')
        let tileXCoord = 148.6
        let tileYCoord = 332.7
        //Our draw come here

        draw(tileXCoord, tileYCoord, ctx)

        function panHandler(event) {
            console.log("Clearing canvas")
            ctx.clearRect(0, 0, canvas.width, canvas.height)
            if (event === "lt-btn") {
                console.log("Panning left")
                tileXCoord = tileXCoord - 1
            } else if (event === "rt-btn") {
                tileXCoord = tileXCoord + 1
            } else if (event === "up-btn") {
                tileYCoord = tileYCoord + 1
            } else if (event === "dn-btn") {
                tileYCoord = tileYCoord - 1
            }
            draw(tileXCoord, tileYCoord, ctx)
        }

        const left = document.getElementById("lt-btn")
        const right = document.getElementById("rt-btn")
        const up = document.getElementById("up-btn")
        const down = document.getElementById("dn-btn")

        left.addEventListener("click", function(){panHandler(left.id)}, false)
        right.addEventListener("click", function(){panHandler(right.id)}, false)
        up.addEventListener("click", function(){panHandler(up.id)}, false)
        down.addEventListener("click", function(){panHandler(down.id)}, false)

    }, [draw])

    return <canvas ref={canvasRef} {...props}/>
}

export default Canvas