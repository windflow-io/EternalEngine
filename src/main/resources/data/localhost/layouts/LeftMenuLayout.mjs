import { FlowArea } from '/modules/coreComponents.mjs';

export default {
    name: 'LeftMenuLayout',
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
        <section class="flex flex-row h-screen bg-gray-900">
            <div class="w-64 bg-purple-500 bg-opacity-25 p-1 text-white">
                <flow-area
                    name="left"
                    :chapters="areas.left.chapters"
                />
            </div>
            <div class="flex-grow bg-blue-500 bg-opacity-25 p-1 text-white">
                <flow-area
                    name="right"
                    :chapters="areas.right.chapters"
                />
            </div>
        </section>
    `,
}
