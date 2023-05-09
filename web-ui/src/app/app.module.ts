import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {FactCheckerComponent} from './fact-checker/fact-checker.component';
import {ApiModule, Configuration} from "../openapi";
import {environment} from "../environments/environment";
import {HttpClientModule} from "@angular/common/http";

@NgModule({
  declarations: [
    AppComponent,
    FactCheckerComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    ApiModule.forRoot(() => new Configuration({
      basePath: environment.apiBaseUrl
    })),
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
