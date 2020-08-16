import {NgModule} from '@angular/core';

import {MainRoutingModule} from './main-routing.module';

import {MainComponent} from './main.component';
import {ConsoleComponent} from '../console/console.component';
import {
    NzAutocompleteModule,
    NzButtonModule,
    NzIconModule,
    NzInputModule,
    NzResultModule,
    NzSelectModule,
    NzSkeletonModule,
    NzTabsModule,
    NzWaveModule
} from 'ng-zorro-antd';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {TerminalComponent} from '../terminal/terminal.component';


@NgModule({
    imports: [MainRoutingModule, NzAutocompleteModule, FormsModule, NzInputModule, NzSelectModule, CommonModule, NzTabsModule, NzIconModule, NzWaveModule, NzButtonModule, NzSkeletonModule, NzResultModule],
    declarations: [MainComponent, ConsoleComponent, TerminalComponent],
    exports: [MainComponent]
})
export class MainModule {
}
