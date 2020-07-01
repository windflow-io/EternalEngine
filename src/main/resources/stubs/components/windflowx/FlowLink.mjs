import {pushUrl} from "/modules/history.mjs";

export default {
    name: 'FlowLink',
    props: ['to'],
    template: '<a :href="to" @click="click($event)"><slot/></a>',
    methods: {
        click(event) {
            pushUrl(this.to)
            event.preventDefault();
        }
    }
}
