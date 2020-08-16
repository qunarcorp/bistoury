import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {TerminalComponent} from './terminal.component';

describe('TerminalComponent', () => {
    let component: TerminalComponent;
    let fixture: ComponentFixture<TerminalComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [TerminalComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(TerminalComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
