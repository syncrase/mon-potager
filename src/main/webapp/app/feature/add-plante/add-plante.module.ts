import {NgModule} from '@angular/core';
import {SharedModule} from "../../shared/shared.module";
import {AddPlanteRoutingModule} from "./route/add-plante-routing.module";
import {SearchPlanteComponent} from "./search/search-plante.component";
import {PlanteUpdateComponent} from "./update/plante-update.component";


@NgModule({
  imports: [
    SharedModule, AddPlanteRoutingModule
  ],
  declarations: [SearchPlanteComponent, PlanteUpdateComponent]
})
export class AddPlanteModule { }
