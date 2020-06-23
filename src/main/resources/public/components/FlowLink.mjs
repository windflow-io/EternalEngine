export default {
    name: 'FlowLink',
    props: ['to'],
    template: '<a :href="to" @click="click($event)"><slot/></a>',
    methods: {
        click(event) {
            history.pushState(null, null, this.to);
            event.preventDefault();
        }
    }
}
