import {Component, OnInit} from '@angular/core';
import {DemoService} from "./app.service";

import { } from '@types/googlemaps';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'app works!';
  data: any = {};

  coordinateData: String[] = [];

  constructor(private demoService: DemoService) {
  }

  ngOnInit() {

    let map;
    const DALLAS = {lat: 48.20368, lng: 16.31764};

    map = new google.maps.Map(document.getElementById('map'), {
      center: DALLAS,
      zoom: 15
    });


    if (this.coordinateData.length === 0) {
      this.getData();
    }

    //google.maps.event.addListener(marker, 'hover', ( () => this.title = "i was a map click") );

    //marker.setMap(this.map);
    //let constMap = this.map;

    var infowindow = new google.maps.InfoWindow();

    var marker;
    var i = 0;


    let keys = Object.keys(this.data);

    for (let coordinate of keys) {
      //coordinate = 123.3 80.3
      let value = this.data[coordinate]
      //value = google : name
              // open : name

      let googleName = value["google"];
      let openName = value["openstreet"];

      let splitCoordinate = coordinate.split(',');

      marker = new google.maps.Marker({
        position: new google.maps.LatLng(Number(splitCoordinate[0]), Number(splitCoordinate[1])),
        map: map
      });

      marker.setMap(map);

      google.maps.event.addListener(marker, 'click', (function(marker, i) {
        return function() {
          infowindow.setContent('location' + i + " - " + "<br />" + "google: " + googleName + "<br />" + "open: " +openName);
          infowindow.open(map, marker);
        }
      })(marker, i));

      i++;

    }


  }

  getData() {
    console.info("in getdata");

    this.demoService.getData().subscribe(
      // the first argument is a function which runs on success
      data => { console.log(data); this.data = data},
      // the second argument is a function which runs on error
      err => console.error(err),
      // the third argument is a function which runs on completion
      () => { console.log('done loading data'); this.ngOnInit(); }
    );


    console.info("out getdata");

  }


}
