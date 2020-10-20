import { FlowArea } from '/modules/coreComponents.js';

export default {
    name: 'SingleColumnLayout',
    components: {
        FlowArea,
    },
    props: {
        areas: {
            required: true,
            type: Object,
        },
    },
    template: `
        <flow-area
            name="content"
            :chapters="areas.content.chapters"
            class="antialiased font-sans bg-gray-200"
        />
    `,
}
