import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FactCheckerComponent} from './fact-checker.component';

describe('FactCheckerComponent', () => {
  let component: FactCheckerComponent;
  let fixture: ComponentFixture<FactCheckerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FactCheckerComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FactCheckerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
