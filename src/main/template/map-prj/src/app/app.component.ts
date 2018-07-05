import {Component, OnInit} from '@angular/core';
import {DemoService} from "./app.service";

import { } from '@types/googlemaps';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'OpenStreetMap Validator';
  data: any = {};

  fileToUpload: File = null;
  isFileUploaded: boolean = false;

  constructor(private demoService: DemoService) {
  }

  ngOnInit() {

    let map;

    if (this.data !== undefined) {
      if (this.data.body !== undefined) {

        this.data = JSON.parse(this.data.body)

        let keys = Object.keys(this.data);

        let splitCoordinateFocus = keys[0].split(',');

        const FOCUS = {lat: Number(splitCoordinateFocus[0]), lng: Number(splitCoordinateFocus[1])};
        debugger

        map = new google.maps.Map(document.getElementById('map'), {
          center: FOCUS,
          zoom: 15
        });

        var infowindow = new google.maps.InfoWindow();

        var marker;
        var i = 0;



        for (let coordinate of keys) {
          //coordinate = 123.3 80.3
          let value = this.data[coordinate]
          //value = google : name
          // open : name

          let googleName = value["google"];
          let openName = value["openstreet"];
          let foursqName = value["foursquare"];
          let microsoftName = value["microsoft"];

          let splitCoordinate = coordinate.split(',');

          marker = new google.maps.Marker({
            position: new google.maps.LatLng(Number(splitCoordinate[0]), Number(splitCoordinate[1])),
            map: map
          });

          marker.setMap(map);

          google.maps.event.addListener(marker, 'click', (function(marker, i) {
            return function() {
              infowindow.setContent('location' + i + " - " + "<br />" + "google: " + googleName +
                "<br />" + "open: " +openName + "<br />" + "foursquare: " + foursqName
                + "<br />" + "microsoft: " + microsoftName );
              infowindow.open(map, marker);
            }
          })(marker, i));

          i++;

        }
      }
    }

  }

  handleFileInput(files: FileList) {
    console.info("in post and get");

    this.fileToUpload = files.item(0);
    this.isFileUploaded = true;
    //TODO success de


    this.demoService.upload(this.fileToUpload).subscribe(
      // the first argument is a function which runs on success
      data => { console.log('check THIS!!' + data); this.data = data
      },
      // the second argument is a function which runs on error
      err => console.error(err),
      // the third argument is a function which runs on completion
      () => { console.log('done loading data'); this.ngOnInit(); }
    );


    console.info("out getdata");


  }

  isEmpty(ob){
    for(var i in ob){ return false;}
    return true;
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
