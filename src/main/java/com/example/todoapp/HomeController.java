package com.example.todoapp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
public class HomeController {

    private static final int PAGE_SIZE = 10;

    private final TodoService todoService;

    public HomeController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "ToDo");
        return "index";
    }

    @GetMapping({ "/todo", "/todos" })
    public String todos(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(defaultValue = "false") boolean showCompleted,
            @RequestParam(defaultValue = "1") int page,
            Model model) {
        int totalCount = todoService.count(keyword, category, showCompleted);
        int totalPages = Math.max(1, (totalCount + PAGE_SIZE - 1) / PAGE_SIZE);
        page = Math.max(1, Math.min(page, totalPages));
        int offset = (page - 1) * PAGE_SIZE;
        model.addAttribute("todos", todoService.search(keyword, category, order, showCompleted, PAGE_SIZE, offset));
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("order", order);
        model.addAttribute("showCompleted", showCompleted);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);
        return "todos";
    }

    @GetMapping("/todos/new")
    public String create(@ModelAttribute("todo") Todo todo) {
        return "create";
    }

    @PostMapping("/todos/confirm")
    public String createConfirm(@Valid @ModelAttribute("todo") Todo todo, BindingResult bindingResult, Model model) {
        model.addAttribute("todo", todo);
        if (bindingResult.hasErrors()) {
            return "create";
        }
        return "create-confirm";
    }

    @PostMapping("/todos")
    public String createTodo(@ModelAttribute("todo") Todo todo, RedirectAttributes redirectAttributes) {
        todoService.create(todo);
        redirectAttributes.addFlashAttribute("message", "登録しました");
        return "redirect:/todos";
    }

    @PostMapping("/todos/new")
    public String createRewrite(@ModelAttribute("todo") Todo todo) {
        return "create";
    }

    @GetMapping("/todos/{id}/edit")
    public String edit(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Todo todo = todoService.findById(id);
        if (todo == null) {
            redirectAttributes.addFlashAttribute("message", "見つかりませんでした");
            return "redirect:/todos";
        }
        model.addAttribute("todo", todo);
        model.addAttribute("id", id);
        return "edit";
    }

    @PostMapping("/todos/{id}/confirm")
    public String editConfirm(@PathVariable("id") Long id, @Valid @ModelAttribute("todo") Todo todo,
            BindingResult bindingResult, Model model) {
        model.addAttribute("todo", todo);
        model.addAttribute("id", id);
        if (bindingResult.hasErrors()) {
            return "edit";
        }
        return "edit-confirm";
    }

    @PostMapping("/todos/{id}/edit")
    public String editRewrite(@PathVariable("id") Long id, @ModelAttribute("todo") Todo todo, Model model) {
        model.addAttribute("todo", todo);
        model.addAttribute("id", id);
        return "edit";
    }

    @PostMapping("/todos/{id}")
    public String updateTodo(@PathVariable("id") Long id, @ModelAttribute("todo") Todo todo,
            RedirectAttributes redirectAttributes) {
        todo.setId(id);
        todoService.update(todo);
        redirectAttributes.addFlashAttribute("message", "保存しました");
        return "redirect:/todos";
    }

    @PostMapping("/todos/{id}/pin")
    public String togglePin(
            @PathVariable("id") Long id,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(defaultValue = "false") boolean showCompleted,
            @RequestParam(defaultValue = "1") int page,
            RedirectAttributes redirectAttributes) {
        Todo todo = todoService.findById(id);
        if (todo != null) {
            todoService.togglePinned(id, !Boolean.TRUE.equals(todo.getPinned()));
        }
        redirectAttributes.addAttribute("keyword", keyword);
        redirectAttributes.addAttribute("category", category);
        redirectAttributes.addAttribute("order", order);
        redirectAttributes.addAttribute("showCompleted", showCompleted);
        redirectAttributes.addAttribute("page", page);
        return "redirect:/todos";
    }

    @GetMapping("/todos/{id}/delete")
    public String deleteConfirm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Todo todo = todoService.findById(id);
        if (todo == null) {
            redirectAttributes.addFlashAttribute("message", "見つかりませんでした");
            return "redirect:/todos";
        }
        model.addAttribute("todo", todo);
        model.addAttribute("id", id);
        return "delete";
    }

    @PostMapping("/todos/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        todoService.delete(id);
        redirectAttributes.addFlashAttribute("message", "削除しました");
        return "redirect:/todos";
    }
}
