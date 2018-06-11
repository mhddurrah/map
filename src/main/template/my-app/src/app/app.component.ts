import {Component, OnInit} from '@angular/core';
import { ViewChild } from '@angular/core';
import { Http, Response } from '@angular/http';
import 'rxjs/add/operator/map';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent extends OnInit {
  title = 'app works!';

  data: any = {};

  private apiUrl = "http://localhost:8080/maps";

  @ViewChild('gmap') gmapElement: any;
  map: google.maps.Map;

  constructor(private http: Http) {
    super();
    this.getData();
  }

  ngOnInit() {

    var mapProp = {
      center: new google.maps.LatLng(48.20368, 16.31764),
      zoom: 15,
      mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    this.map = new google.maps.Map(this.gmapElement.nativeElement, mapProp);

    let marker = new google.maps.Marker({
      position: {lat:48.2060362,lng:16.3218400},
      map: this.map
    });

    //google.maps.event.addListener(marker, 'hover', ( () => this.title = "i was a map click") );

    console.log(this.data);

    //marker.setMap(this.map);

  }

  getData() {
    console.info("in getdata");
    return this.http.get(this.apiUrl)
      .map((res: Response) => res.json());
  }

}
