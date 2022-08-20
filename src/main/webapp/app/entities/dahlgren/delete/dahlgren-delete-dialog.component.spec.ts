jest.mock('@ng-bootstrap/ng-bootstrap');

import {ComponentFixture, fakeAsync, inject, TestBed, tick} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {of} from 'rxjs';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

import {DahlgrenService} from '../service/dahlgren.service';

import {DahlgrenDeleteDialogComponent} from './dahlgren-delete-dialog.component';

describe('Dahlgren Management Delete Component', () => {
  let comp: DahlgrenDeleteDialogComponent;
  let fixture: ComponentFixture<DahlgrenDeleteDialogComponent>;
  let service: DahlgrenService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [DahlgrenDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(DahlgrenDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(DahlgrenDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(DahlgrenService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('Should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete(123);
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith(123);
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      })
    ));

    it('Should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
