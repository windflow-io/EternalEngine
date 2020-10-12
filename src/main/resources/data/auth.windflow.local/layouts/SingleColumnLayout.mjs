import { FlowArea } from '/modules/coreComponents.mjs';

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
            :area-components="areas.content.components"
            class="antialiased font-sans bg-gray-200"
        />
    `,
}
