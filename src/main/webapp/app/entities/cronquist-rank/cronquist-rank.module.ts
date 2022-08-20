import {NgModule} from '@angular/core';
import {SharedModule} from 'app/shared/shared.module';
import {CronquistRankComponent} from './list/cronquist-rank.component';
import {CronquistRankDetailComponent} from './detail/cronquist-rank-detail.component';
import {CronquistRankUpdateComponent} from './update/cronquist-rank-update.component';
import {CronquistRankDeleteDialogComponent} from './delete/cronquist-rank-delete-dialog.component';
import {CronquistRankRoutingModule} from './route/cronquist-rank-routing.module';

@NgModule({
  imports: [SharedModule, CronquistRankRoutingModule],
  declarations: [CronquistRankComponent, CronquistRankDetailComponent, CronquistRankUpdateComponent, CronquistRankDeleteDialogComponent],
  entryComponents: [CronquistRankDeleteDialogComponent],
})
export class CronquistRankModule {}
