jest.mock('@ng-bootstrap/ng-bootstrap');

import {ComponentFixture, fakeAsync, inject, TestBed, tick} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {of} from 'rxjs';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

import {ClassificationService} from '../service/classification.service';

import {ClassificationDeleteDialogComponent} from './classification-delete-dialog.component';

describe('Classification Management Delete Component', () => {
  let comp: ClassificationDeleteDialogComponent;
  let fixture: ComponentFixture<ClassificationDeleteDialogComponent>;
  let service: ClassificationService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ClassificationDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(ClassificationDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ClassificationDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ClassificationService);
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
