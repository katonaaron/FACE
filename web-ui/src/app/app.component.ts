import {Component} from '@angular/core';
import {FactCheckerService} from "../openapi";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'fact-checker-ui';

  constructor(private service: FactCheckerService) {
    service.performFactChecking("Antibiotics kill bacteria")
      .subscribe(
        res => console.log(res)
      )
  }
}
