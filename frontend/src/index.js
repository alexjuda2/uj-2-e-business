import React from 'react';
import { render } from 'react-dom';

import App from './comp/App';
import { exampleRequest } from "./api";

render(<App />, document.getElementById('root'));

window.ebiz = {
    exampleRequest,
}
