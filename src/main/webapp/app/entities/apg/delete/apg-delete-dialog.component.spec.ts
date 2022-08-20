jest.mock('@ng-bootstrap/ng-bootstrap');

import {ComponentFixture, fakeAsync, inject, TestBed, tick} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {of} from 'rxjs';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

import {APGService} from '../service/apg.service';

import {APGDeleteDialogComponent} from './apg-delete-dialog.component';

describe('APG Management Delete Component', () => {
  let comp: APGDeleteDialogComponent;
  let fixture: ComponentFixture<APGDeleteDialogComponent>;
  let service: APGService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [APGDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(APGDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(APGDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(APGService);
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
