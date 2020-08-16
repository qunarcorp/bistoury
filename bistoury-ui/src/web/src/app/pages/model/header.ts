export class Header {
    name: string;
    href: string;
    selected: boolean = false;
    child: Array<Header>


    constructor(name: string, href: string, selected: boolean, child: Array<Header>) {
        this.name = name;
        this.href = href;
        this.selected = selected;
        this.child = child;
    }
}
