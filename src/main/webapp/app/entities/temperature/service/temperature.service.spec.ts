import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITemperature, Temperature } from '../temperature.model';

import { TemperatureService } from './temperature.service';

describe('Temperature Service', () => {
  let service: TemperatureService;
  let httpMock: HttpTestingController;
  let elemDefault: ITemperature;
  let expectedResult: ITemperature | ITemperature[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TemperatureService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      min: 0,
      max: 0,
      description: 'AAAAAAA',
      rusticite: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Temperature', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Temperature()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Temperature', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          min: 1,
          max: 1,
          description: 'BBBBBB',
          rusticite: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Temperature', () => {
      const patchObject = Object.assign(
        {
          min: 1,
          description: 'BBBBBB',
        },
        new Temperature()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Temperature', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          min: 1,
          max: 1,
          description: 'BBBBBB',
          rusticite: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Temperature', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTemperatureToCollectionIfMissing', () => {
      it('should add a Temperature to an empty array', () => {
        const temperature: ITemperature = { id: 123 };
        expectedResult = service.addTemperatureToCollectionIfMissing([], temperature);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(temperature);
      });

      it('should not add a Temperature to an array that contains it', () => {
        const temperature: ITemperature = { id: 123 };
        const temperatureCollection: ITemperature[] = [
          {
            ...temperature,
          },
          { id: 456 },
        ];
        expectedResult = service.addTemperatureToCollectionIfMissing(temperatureCollection, temperature);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Temperature to an array that doesn't contain it", () => {
        const temperature: ITemperature = { id: 123 };
        const temperatureCollection: ITemperature[] = [{ id: 456 }];
        expectedResult = service.addTemperatureToCollectionIfMissing(temperatureCollection, temperature);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(temperature);
      });

      it('should add only unique Temperature to an array', () => {
        const temperatureArray: ITemperature[] = [{ id: 123 }, { id: 456 }, { id: 31513 }];
        const temperatureCollection: ITemperature[] = [{ id: 123 }];
        expectedResult = service.addTemperatureToCollectionIfMissing(temperatureCollection, ...temperatureArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const temperature: ITemperature = { id: 123 };
        const temperature2: ITemperature = { id: 456 };
        expectedResult = service.addTemperatureToCollectionIfMissing([], temperature, temperature2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(temperature);
        expect(expectedResult).toContain(temperature2);
      });

      it('should accept null and undefined values', () => {
        const temperature: ITemperature = { id: 123 };
        expectedResult = service.addTemperatureToCollectionIfMissing([], null, temperature, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(temperature);
      });

      it('should return initial array if no Temperature is added', () => {
        const temperatureCollection: ITemperature[] = [{ id: 123 }];
        expectedResult = service.addTemperatureToCollectionIfMissing(temperatureCollection, undefined, null);
        expect(expectedResult).toEqual(temperatureCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
