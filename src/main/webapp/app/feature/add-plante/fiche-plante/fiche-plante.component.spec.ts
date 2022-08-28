import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FichePlanteComponent} from './fiche-plante.component';

describe('FichePlanteComponent', () => {
  let component: FichePlanteComponent;
  let fixture: ComponentFixture<FichePlanteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FichePlanteComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FichePlanteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
