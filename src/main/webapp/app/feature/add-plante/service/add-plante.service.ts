import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApplicationConfigService} from 'app/core/config/application-config.service';
import {IPlante} from "../../../entities/plante/plante.model";

export type EntityResponseType = HttpResponse<IPlante>;
export type EntityArrayResponseType = HttpResponse<IPlante[]>;

@Injectable({providedIn: 'root'})
export class AddPlanteService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/plantes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {
  }

  search(plante: string): Observable<EntityResponseType> {
    return this.http.get<IPlante>(`${this.resourceUrl}/scrap`, {observe: 'response', params: {name: plante}});
  }

}
