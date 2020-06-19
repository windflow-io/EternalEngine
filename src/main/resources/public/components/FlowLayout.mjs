import FlowArea from '/components/FlowArea.mjs';

export default {
    name: 'FlowLayout',
    template: '',
    components: {FlowArea},
    data() {
        return {
            display: {}
        }
    },
    mounted() {
        console.log ("Flow Layout Mounted");
    }
}
