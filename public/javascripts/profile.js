$(document).ready(() => {
    /* Add or subtract 1 to a skill */
    let clickEndorsement = function ($skill) {
        $.get({
            url: $skill.data("url"),
            dataType: 'json',
            success: function (data) {
                $skill.find(".skills__nb-endorsement")
                    .text(data)
                    .toggleClass("skills__nb-endorsement--endorsed")
                    .toggleClass("skills__icon-endorsement")
                    .toggleClass("skills__icon-endorsement--" + $skill.data("size"));
            }
        });
    };
    $(".skills__endorsable").click(function () {
        clickEndorsement($(this));
    });

    /* Show or hide the suggestion area */
    let $skillSuggestButton = $(".skills__suggest-button");
    let $skillSuggest = $(".skills__suggest");

    $skillSuggestButton.click(function () {
        $(this).addClass("skills__suggest-button--hidden");
        $skillSuggest.removeClass("skills__suggest--hidden");
    });

    $(".skills__cancel-suggest").click(function () {
        $(this).siblings("input").val("");
        $skillSuggest.addClass("skills__suggest--hidden");
        $skillSuggestButton.removeClass("skills__suggest-button--hidden");
    });

    /* Send the suggested skill */
    $(".skills__submit-suggest").click(function () {
        let $this = $(this);
        let skill = $this.siblings("input").val();
        if (skill.trim().length > 0) {
            $.post({
                url: $this.data("url"),
                contentType: "application/json",
                data: JSON.stringify({skill: skill}),
                dataType: 'json',
                success: function (skill) {
                    let $skillsItems = $(".skills__item:not(.skills__item--hidden)");
                    let $existingSkill = $skillsItems.filter(function () {
                        return $(this).find(".skills__name").text() === skill.name;
                    });
                    if ($existingSkill.length === 0) {
                        let size = $skillsItems.length > 5 ? "small" : "big";
                        let $newSkill = $("#template-skills_item").clone();
                        $newSkill.removeClass("skills__item--hidden")
                            .removeAttr("id")
                            .data("url", $newSkill.data("url").substr(0, $newSkill.data("url").length - 1) + skill.id)
                            .data("size", size)
                            .click(function () {
                                clickEndorsement($(this));
                            });
                        $newSkill.find(".skills__nb-endorsement")
                            .text(skill.nbEndorsement)
                            .addClass("skills__nb-endorsement--" + size);
                        $newSkill.find(".skills__name").text(skill.name);
                        if ($skillsItems.length <= 5) {
                            $(".skills__list-items").append("<li>");
                            $(".skills__list-items li").last().append($newSkill);
                        } else if ($skillsItems.length === 6) {
                            $(".skills__list-items").after($newSkill);
                        } else {
                            $skillsItems.last().after($newSkill);
                        }

                        $(".skills__no-skill").addClass("skills__no-skill--hidden");
                    } else {
                        $existingSkill.find(".skills__nb-endorsement")
                            .text(skill.nbEndorsement)
                            .addClass("skills__nb-endorsement--endorsed")
                            .removeClass("skills__icon-endorsement skills__icon-endorsement--" + $(this).data("size"));
                    }
                    $(".skills__cancel-suggest").trigger("click");
                }
            });
        }
    });

    /* Show the edit area of the user */
    let $cancelledSelect;
    $(".skills__list-area--editable, .skills__edit-button").click(function () {
        $cancelledSelect = $(".skills__edit-area select").clone();
        $(".skills__list-area").addClass("skills__list-area--hidden");
        $(".skills__edit-button").addClass("skills__list-area--hidden");
        $(".skills__edit-area").removeClass("skills__edit-area--hidden");

        $("#skills-select").select2({
            multiple: true,
            tags: true,
            width: "100%",
            placeholder: "Input skills...",
            ajax: {
                url: $("#skills-select").data("url"),
                minimumInputLength: 2,
                type: "POST",
                dataType: 'json',
                delay: 250,
                data: function (term) {
                    return {
                        q: term
                    };
                },
                results: function (data) {
                    console.log(data);
                    return {results: data};
                },
            }
        });
    });

    /* Hide the edit area of the user */
    $(".skills__edit-cancel").click(function () {
        $(".skills__edit-area select").replaceWith($cancelledSelect).trigger("change");
        $(".skills__edit-area").addClass("skills__edit-area--hidden");
        $(".skills__list-area").removeClass("skills__list-area--hidden");
        $(".skills__edit-button").removeClass("skills__list-area--hidden");
    });

    /* Save the skills */
    $(".skills__edit-submit").click(function () {
        let $this = $(this);
        let listSkills = [];

        $('#skills-select').find(":selected").each(function (i, selected) {
            listSkills[i] = $(selected).val();
        });

        $.post({
            url: $this.data("url"),
            contentType: "application/json",
            data: JSON.stringify({skills: listSkills}),
            dataType: 'json',
            success: function (skills) {
                if (skills.length === 0) {
                    $(".skills__no-skill").removeClass("skills__no-skill--hidden");
                } else {
                    $(".skills__no-skill").addClass("skills__no-skill--hidden");
                }

                $(".skills__list-items").html("");
                $(".skills__item:not(#template-skills_item)").remove();
                $.each(skills, function (index, skill) {
                    let $skill = $("#template-skills_item").clone();
                    let size = index > 5 ? "small" : "big";
                    $skill.removeClass("skills__item--hidden")
                        .removeAttr("id")
                        .data("url", "/profile/endorse-skill/" + skill.id)
                        .data("size", size)
                        .click(function () {
                            clickEndorsement($(this));
                        });

                    $skill.find(".skills__nb-endorsement")
                        .text(skill.nbEndorsement)
                        .addClass("skills__nb-endorsement--" + size)
                        .removeClass("skills__nb-endorsement--endorsed");

                    $skill.find(".skills__name")
                        .text(skill.name);

                    if (index <= 5) {
                        $(".skills__list-items").append("<li>");
                        $(".skills__list-items li").last().append($skill);
                    } else if (index === 6) {
                        $(".skills__list-items").after($skill);
                    } else {
                        $(".skills__item").last().after($skill);
                    }
                });

                $(".skills__edit-area").addClass("skills__edit-area--hidden");
                $(".skills__list-area").removeClass("skills__list-area--hidden");
                $(".skills__edit-button").removeClass("skills__list-area--hidden");
            }
        });
    });
});