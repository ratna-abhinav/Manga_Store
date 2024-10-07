$(function() {

    // User Register validation
    var $userRegister = $("#userRegister");

    $userRegister.validate({
        rules: {
            name: {
                required: true,
                lettersonly: true
            },
            email: {
                required: true,
                space: true,
                email: true
            },
            mobileNumber: {
                required: true,
                space: true,
                numericOnly: true,
                minlength: 10,
                maxlength: 12
            },
            password: {
                required: true,
                space: true
            },
            confirmpassword: {
                required: true,
                space: true,
                equalTo: '#checkpass'
            },
            address: {
                required: true,
                all: true
            },
            city: {
                required: true,
                space: true
            },
            state: {
                required: true
            },
            pincode: {
                required: true,
                space: true,
                numericOnly: true
            },
            img: {
                required: true
            }
        },
        messages: {
            name: {
                required: 'Name is required',
                lettersonly: 'Invalid name'
            },
            email: {
                required: 'Email is required',
                space: 'Spaces are not allowed',
                email: 'Invalid email'
            },
            mobileNumber: {
                required: 'Mobile number is required',
                space: 'Spaces are not allowed',
                numericOnly: 'Invalid mobile number',
                minlength: 'Minimum 10 digits',
                maxlength: 'Maximum 12 digits'
            },
            password: {
                required: 'Password is required',
                space: 'Spaces are not allowed'
            },
            confirmpassword: {
                required: 'Confirm password is required',
                space: 'Spaces are not allowed',
                equalTo: 'Password mismatch'
            },
            address: {
                required: 'Address is required',
                all: 'Invalid address'
            },
            city: {
                required: 'City is required',
                space: 'Spaces are not allowed'
            },
            state: {
                required: 'State is required'
            },
            pincode: {
                required: 'PinCode is required',
                space: 'Spaces are not allowed',
                numericOnly: 'Invalid PinCode'
            },
            img: {
                required: 'Image is required'
            }
        }
    });

    // Orders Validation
    var $orders = $("#orders");
    $orders.validate({
        rules: {
            firstName: {
                required: true,
                lettersonly: true
            },
            lastName: {
                required: true,
                lettersonly: true
            },
            email: {
                required: true,
                space: true,
                email: true
            },
            mobileNo: {
                required: true,
                space: true,
                numericOnly: true,
                minlength: 10,
                maxlength: 12
            },
            address: {
                required: true,
                all: true
            },
            city: {
                required: true,
                space: true
            },
            state: {
                required: true
            },
            pincode: {
                required: true,
                space: true,
                numericOnly: true
            },
            paymentType: {
                required: true
            }
        },
        messages: {
            firstName: {
                required: 'First name is required',
                lettersonly: 'Invalid name'
            },
            lastName: {
                required: 'Last name is required',
                lettersonly: 'Invalid name'
            },
            email: {
                required: 'Email is required',
                space: 'Spaces are not allowed',
                email: 'Invalid email'
            },
            mobileNo: {
                required: 'Mobile number is required',
                space: 'Spaces are not allowed',
                numericOnly: 'Invalid mobile number',
                minlength: 'Minimum 10 digits',
                maxlength: 'Maximum 12 digits'
            },
            address: {
                required: 'Address is required',
                all: 'Invalid address'
            },
            city: {
                required: 'City is required',
                space: 'Spaces are not allowed'
            },
            state: {
                required: 'State is required'
            },
            pincode: {
                required: 'PinCode is required',
                space: 'Spaces are not allowed',
                numericOnly: 'Invalid PinCode'
            },
            paymentType: {
                required: 'Please select a payment type'
            }
        }
    });

    // Reset Password Validation
    var $resetPassword = $("#resetPassword");
    $resetPassword.validate({
        rules: {
            password: {
                required: true,
                space: true
            },
            confirmPassword: {
                required: true,
                space: true,
                equalTo: '#checkpass'
            }
        },
        messages: {
            password: {
                required: 'Password is required',
                space: 'Spaces are not allowed'
            },
            confirmPassword: {
                required: 'Confirm password is required',
                space: 'Spaces are not allowed',
                equalTo: 'Password mismatch'
            }
        }
    });

    // Custom validation methods
    jQuery.validator.addMethod('lettersonly', function(value, element) {
        return /^[^-\s][a-zA-Z_\s-]+$/.test(value);
    });

    jQuery.validator.addMethod('space', function(value, element) {
        return /^[^\s]+(\s[^\s]+)*$/.test(value);  // This allows spaces between words, but no leading/trailing spaces
    });

    jQuery.validator.addMethod('all', function(value, element) {
        return /^[^-\s][a-zA-Z0-9_,.\s-]+$/.test(value);
    });

    jQuery.validator.addMethod('numericOnly', function(value, element) {
        return /^[0-9]+$/.test(value);
    });
});
