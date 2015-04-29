(function($) {
	var delimiter = new Array();
	var tags_callbacks = new Array();

	$.fn.addTag = function(value,options) {
	  options = jQuery.extend({focus:false,callback:true},options);
		this.each(function() {
			var id = $(this).attr('id');

			var tagslist = $(this).val().split(delimiter[id]);
			if (tagslist[0] == '') {
				tagslist = new Array();
			}

			value = jQuery.trim(value);

			if (options.unique) {
				var skipTag = $(this).tagExist(value);
				if(skipTag == true) {
					//Marks fake input as not_valid to let styling it
    			$('#'+id+'-tag').addClass('not_valid');
    		}
			} else {
				var skipTag = false;
			}

			if (value !='' && skipTag != true) {
        $('<div>').addClass('label label-primary').append(
          $('<span>').text(value).append('&nbsp;&nbsp;'),
          $('<i>').addClass('fa fa-close')
            .click(function() {
              return $('#' + id).removeTag(escape(value));
            })
        ).insertBefore('#' + id + '-addTag');

				tagslist.push(value);

				$('#'+id+'-tag').val('');
				if (options.focus) {
					$('#'+id+'-tag').focus();
				} else {
					$('#'+id+'-tag').blur();
				}

				$.fn.tagsInput.updateTagsField(this,tagslist);

				if (options.callback && tags_callbacks[id] && tags_callbacks[id]['onAddTag']) {
					var f = tags_callbacks[id]['onAddTag'];
					f.call(this, value);
				}

				if(tags_callbacks[id] && tags_callbacks[id]['onChange'])
				{
					var i = tagslist.length;
					var f = tags_callbacks[id]['onChange'];
					f.call(this, $(this), tagslist[i-1]);
				}
			}

      $("#tags").val($(this).val());
      var tagslist = $(this).val().split(delimiter[id]);
      if (tagslist.length >= 5) {
        $('#tags-addTag').css('display', 'none');
      }
      else {
        $('#tags-addTag').css('display', 'block');
      }

		});

		return false;
	};

	$.fn.removeTag = function(value) {
		value = unescape(value);
		this.each(function() {
			var id = $(this).attr('id');

			var old = $(this).val().split(delimiter[id]);

			$('#'+id+'-tagsinput .label').remove();
			str = '';
			for (i=0; i< old.length; i++) {
				if (old[i]!=value) {
					str = str + delimiter[id] +old[i];
				}
			}

			$.fn.tagsInput.importTags(this,str);

			if (tags_callbacks[id] && tags_callbacks[id]['onRemoveTag']) {
				var f = tags_callbacks[id]['onRemoveTag'];
				f.call(this, value);
			}
		});

		return false;
	};

	$.fn.tagExist = function(val) {
		var id = $(this).attr('id');
		var tagslist = $(this).val().split(delimiter[id]);
		return (jQuery.inArray(val, tagslist) >= 0); //true when tag exists, false when not
	};

	// clear all existing tags and import new ones from a string
	$.fn.importTags = function(str) {
    id = $(this).attr('id');
		$('#'+id+'-tagsinput .label').remove();
		$.fn.tagsInput.importTags(this,str);
	};

	$.fn.tagsInput = function(options) {
    var settings = jQuery.extend({
      interactive:true,
      defaultText:'添加标签，多个标签用空格分隔，帮助你更好地整理内容',
      minChars:0,
      width:'100%',
      height:'auto',
      autocomplete: {selectFirst: false },
      'delimiter':',',
      'unique':true,
      removeWithBackspace:false,
      placeholderColor:'#666666',
      autosize: true,
      comfortZone: 20,
      inputPadding: 6*2
    },options);

		this.each(function() {
			var id = $(this).attr('id');
			if (!id || delimiter[$(this).attr('id')]) {
				id = $(this).attr('id', 'tags' + new Date().getTime()).attr('id');
			}

			var data = jQuery.extend({
				pid:id,
				real_input: '#'+id,
				holder: '#'+id+'-tagsinput',
				input_wrapper: '#'+id+'-addTag',
				fake_input: '#'+id+'-tag'
			},settings);

			delimiter[id] = data.delimiter;

			if (settings.onAddTag || settings.onRemoveTag || settings.onChange) {
				tags_callbacks[id] = new Array();
				tags_callbacks[id]['onAddTag'] = settings.onAddTag;
				tags_callbacks[id]['onRemoveTag'] = settings.onRemoveTag;
				tags_callbacks[id]['onChange'] = settings.onChange;
			}

			var markup = '<div id="'+id+'-tagsinput" class="tagsinput"><div id="'+id+'-addTag">';

			if (settings.interactive) {
				markup = markup + '<input id="'+id+'-tag" value="" type="text" data-default="'+settings.defaultText+'" />';
			}

			markup = markup + '</div><div class="tags-clear"></div></div>';

			$(markup).insertAfter(this);

			$(data.holder).css('width',settings.width);
			$(data.holder).css('min-height',settings.height);
			$(data.holder).css('height','100%');

			if ($(data.real_input).val()!='') {
				$.fn.tagsInput.importTags($(data.real_input),$(data.real_input).val());
			}
			if (settings.interactive) {
				$(data.fake_input).val($(data.fake_input).attr('data-default'));
				$(data.fake_input).css('color',settings.placeholderColor);
		    // $(data.fake_input).resetAutosize(settings);

				$(data.holder).bind('click',data,function(event) {
					$(event.data.fake_input).focus();
				});

				$(data.fake_input).bind('focus',data,function(event) {
					if ($(event.data.fake_input).val()==$(event.data.fake_input).attr('data-default')) {
						$(event.data.fake_input).val('');
					}
					$(event.data.fake_input).css('color','#000000');
				});

        $(data.fake_input).keyup(function() {
          var val = $.trim($(data.fake_input).val());
          var lastChar = val ? val.substr(-1, 1) : '';
          if (lastChar == '，') {
            $(data.fake_input).val(val);
            $(data.fake_input).trigger('keypress', [44]);
          }
        });

				// if user types a comma, create a new tag
				$(data.fake_input).bind('keypress',data,function(event, keyCode) {
          var code = keyCode || event.keyCode || event.which;
					if (code==44 || code==13) {
					  event.preventDefault();

            var val = $.trim($(data.fake_input).val());
            var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\]%.<>/?·～！@#￥……&*（）——|{}【】『』《》‘；：”“’‘'。，、？×+]", "g");
            val = val.replace(pattern, '');
            $(data.fake_input).val(val);

						if((event.data.minChars <= $(event.data.fake_input).val().length) && (!event.data.maxChars || (event.data.maxChars >= $(event.data.fake_input).val().length)))
							$(event.data.real_input).addTag($(event.data.fake_input).val(),{focus:true,unique:(settings.unique)});
						return false;
					}});

				//Delete last tag on backspace
				data.removeWithBackspace && $(data.fake_input).bind('keydown', function(event) {
					if(event.keyCode == 8 && $(this).val() == '')
					{
						event.preventDefault();
						var last_tag = $(this).closest('.tagsinput').find('.label:last').text();
						var id = $(this).attr('id').replace(/-tag$/, '');
						last_tag = last_tag.replace(/[\s]+x$/, '');
						$('#' + id).removeTag(escape(last_tag));
						$(this).trigger('focus');
					}
				});
				$(data.fake_input).blur();

				//Removes the not_valid class when user changes the value of the fake input
				if(data.unique) {
				  $(data.fake_input).keydown(function(event){
				    if(event.keyCode == 8 || String.fromCharCode(event.which).match(/\w+|[áéíóúÁÉÍÓÚñÑ,/]+/)) {
				      $(this).removeClass('not_valid');
				    }
				  });
				}
			} // if settings.interactive
		});

		return this;

	};

	$.fn.tagsInput.updateTagsField = function(obj,tagslist) {
		var id = $(obj).attr('id');
		$(obj).val(tagslist.join(delimiter[id]));
	};

	$.fn.tagsInput.importTags = function(obj,val) {
		$(obj).val('');
		var id = $(obj).attr('id');
		var tags = val.split(delimiter[id]);
		for (i=0; i<tags.length; i++) {
			$(obj).addTag(tags[i],{focus:false,callback:false});
		}
		if(tags_callbacks[id] && tags_callbacks[id]['onChange'])
		{
			var f = tags_callbacks[id]['onChange'];
			f.call(obj, obj, tags[i]);
		}
	};

})(jQuery);
